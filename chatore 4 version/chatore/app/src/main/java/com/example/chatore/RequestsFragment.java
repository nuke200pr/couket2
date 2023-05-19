package com.example.chatore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment {

    private RecyclerView rvRequests;
    private RequestAdapter adapter;
    private List<RequestModel> requestModelList;
    private TextView tvEmptyRequestView;


    private DatabaseReference databaseReferenceRequests,databaseReferenceUsers;
    private FirebaseUser currentuser;
    private View progressBar;



    public RequestsFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRequests = view.findViewById(R.id.rvRequests);
        tvEmptyRequestView = view.findViewById(R.id.tvEmptyChatList);
        progressBar = view.findViewById(R.id.fragment_requests_progress_bar);

        rvRequests.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestModelList = new ArrayList<>();
        adapter = new RequestAdapter(getActivity(),requestModelList);

        rvRequests.setAdapter(adapter);

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(nodenames.USERS);

        databaseReferenceRequests = FirebaseDatabase.getInstance().getReference().child(nodenames.FRIEND_REQUESTS).child(currentuser.getUid());

        tvEmptyRequestView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        databaseReferenceRequests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                requestModelList.clear();
                for(DataSnapshot ds :snapshot.getChildren() ){
                    if(ds.exists())
                    {
                        String requestType = ds.child(nodenames.REQUEST_TYPE).getValue().toString();
                        if(requestType.equals(Constants.REQUEST_STATUS_RECEIVED))
                        {
                            String userId =ds.getKey();

                            databaseReferenceUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String username="";



                                          if(snapshot.child(nodenames.NAME).getValue()!=null) {
                                              username = snapshot.child(nodenames.NAME).getValue().toString();
                                          }
                                    String photoName="";

                                    if(snapshot.child(nodenames.PHOTO).getValue()!=null)
                                    {
                                        photoName = "images/"+userId+".jpg";;
                                    }

                                    RequestModel requestModel = new RequestModel(userId,username,photoName);
                                    requestModelList.add(requestModel);
                                    adapter.notifyDataSetChanged();
                                    tvEmptyRequestView.setVisibility(View.GONE);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.failed_fetch_requests,error.getMessage()), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);


                                }
                            });
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), getActivity().getString(R.string.failed_fetch_requests,error.getMessage()), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);


            }
        });

    }
}