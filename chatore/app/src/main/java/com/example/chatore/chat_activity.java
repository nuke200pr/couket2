package com.example.chatore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.internal.Util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class chat_activity extends AppCompatActivity implements View.OnClickListener{

    private ImageView ivSend;
    private EditText etMessage;
    private DatabaseReference mRootRef;

    private FirebaseAuth firebaseAuth;
    private String currentUserId,chatUserId;

    private RecyclerView rvMessages;
    private SwipeRefreshLayout srlMessages;
    private MessagesAdapter messagesAdapter;
    private List<MessageModel> messagesList ;

    private int currentPage=1;
    private static final int RECORD_PER_PAGE=30;

    private DatabaseReference databaseReferenceMessages;
    private ChildEventListener childEventListener;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ivSend = findViewById(R.id.ivSend);
        etMessage = findViewById(R.id.etMessage);

        ivSend.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        if(getIntent().hasExtra(Extras.USER_KEY))
        {
            chatUserId = getIntent().getStringExtra(Extras.USER_KEY);
        }

        rvMessages =findViewById(R.id.rvMessages);
        srlMessages=findViewById(R.id.srlMessages);

        messagesList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this,messagesList);

        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(messagesAdapter);

        loadMessages();
        rvMessages.scrollToPosition(messagesList.size()-1);

        srlMessages.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                loadMessages();
            }
        });

    }

    private void sendMessage(String msg ,String msgType,String pushId)
    {
        try {

            if(!msg.equals(""))
            {
                HashMap messageApp = new HashMap();
                messageApp.put(nodenames.MESSAGE_ID,pushId);
                messageApp.put(nodenames.MESSAGE,msg);
                messageApp.put(nodenames.MESSAGE_TYPE,msgType);
                messageApp.put(nodenames.MESSAGE_FROM,currentUserId);
                messageApp.put(nodenames.MESSAGE_TIME, ServerValue.TIMESTAMP);

                String currentUserRef = nodenames.MESSAGES + "/"+currentUserId+"/"+chatUserId;
                String chatUserRef = nodenames.MESSAGES+"/"+chatUserId+"/"+currentUserId;

                HashMap messageUserMap = new HashMap();
                messageUserMap.put(currentUserRef+"/"+pushId,messageApp);
                messageUserMap.put(chatUserRef+"/"+pushId,messageApp);

                etMessage.setText("");

                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                        if(error!=null)
                        {
                            Toast.makeText(chat_activity.this, getString(R.string.failed_to_send_message,error.getMessage()), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(chat_activity.this, R.string.message_sent_successfully, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        }
        catch (Exception ex)
        {
            Toast.makeText(chat_activity.this, getString(R.string.failed_to_send_message,ex.getMessage()), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMessages()
    {
        messagesList.clear();
        databaseReferenceMessages =mRootRef.child(nodenames.MESSAGES).child(currentUserId).child(chatUserId);

        Query messageQuery = databaseReferenceMessages.limitToLast(currentPage*RECORD_PER_PAGE);

        if(childEventListener!=null)
        {
            messageQuery.removeEventListener(childEventListener);
        }
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                MessageModel message = snapshot.getValue(MessageModel.class);


                messagesList.add(message);
                messagesAdapter.notifyDataSetChanged();
                rvMessages.scrollToPosition(messagesList.size()-1);
                srlMessages.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                srlMessages.setRefreshing(false);
            }
        };

        messageQuery.addChildEventListener(childEventListener);
    }
    @Override
    public void onClick(View view) {

        switch(view.getId())
        {
            case R.id.ivSend:



                if(util.connectionAvailable(this)) {
                    DatabaseReference userMessagePush = mRootRef.child(nodenames.MESSAGES).child(currentUserId).child(chatUserId).push();
                    String pushId = userMessagePush.getKey();
                    sendMessage(etMessage.getText().toString().trim(), Constants.MESSAGE_TYPE_TEXT, pushId);
                }
                else
                {
                    Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
        }

    }
}