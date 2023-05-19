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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment {

    private RecyclerView rvFindFriends;
    private FindFriendAdapter findFriendsAdapter;
    private List<FindFriendModel> findFriendModelList;
    private TextView tvEmptyFriendsList;

    private DatabaseReference databaseReference, databaseReferenceFriendRequests;
    private FirebaseUser currentUser;
    private View progressBar;


    public FriendsFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFindFriends = view.findViewById(R.id.rvFriends);
        progressBar = view.findViewById(R.id.progressBar_friends);
        tvEmptyFriendsList = view.findViewById(R.id.tvEmptyChatList);

        rvFindFriends.setLayoutManager(new LinearLayoutManager(getActivity()));

        findFriendModelList = new ArrayList<>();
        findFriendsAdapter = new FindFriendAdapter(getActivity(),findFriendModelList);
        rvFindFriends.setAdapter(findFriendsAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(nodenames.USERS);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReferenceFriendRequests = FirebaseDatabase.getInstance().getReference().child(nodenames.FRIEND_REQUESTS).child(currentUser.getUid());

        tvEmptyFriendsList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Query query = databaseReference.orderByChild(nodenames.NAME);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                findFriendModelList.clear();
                for(DataSnapshot ds : snapshot.getChildren() )
                {
                    String userID = ds.getKey();
                    if(userID.equals(currentUser.getUid()))
                    {
                         continue;
                    }
                    if(ds.child(nodenames.NAME)!=null)
                    {
                        String fullName= ds.child(nodenames.NAME).getValue().toString();


                        String photoName;
                        if(ds.child(nodenames.PHOTO).getValue()==null)
                        {
                            photoName = "";
                        }
                        else {
                            photoName = "images/"+userID+".jpg";
                        }



                        databaseReferenceFriendRequests.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    String requestType  = snapshot.child(nodenames.REQUEST_TYPE).getValue().toString();
                                    if(requestType.equals(Constants.REQUEST_STATUS_SENT))
                                    {
                                        findFriendModelList.add(new FindFriendModel(fullName,photoName,userID,true));
                                        findFriendsAdapter.notifyDataSetChanged();
                                    }

                                }
                                else
                                {
                                    findFriendModelList.add(new FindFriendModel(fullName,photoName,userID,false));
                                    findFriendsAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                progressBar.setVisibility(View.GONE);

                            }
                        });


                        tvEmptyFriendsList.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);



                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), getContext().getString(R.string.failed_fetch_friends,error.getMessage()), Toast.LENGTH_SHORT).show();

            }
        });
    }
}