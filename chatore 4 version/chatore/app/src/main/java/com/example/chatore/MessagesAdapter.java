package com.example.chatore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
//import android.view.ActionMode;
import androidx.appcompat.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private Context context;
    private List<MessageModel> messageList;
    private FirebaseAuth firebaseAuth;

    private ActionMode actionMode;
    private ConstraintLayout selectedView;

    public MessagesAdapter(Context context, List<MessageModel> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessagesAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view = LayoutInflater.from(context).inflate(R.layout.message_layout,parent,false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MessageViewHolder holder, int position) {

        MessageModel message = messageList.get(position);
        firebaseAuth =FirebaseAuth.getInstance();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String fromUserId= message.getMessageFrom();

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        String dateTime = sfd.format(new Date(message.getMessageTime()));
        String [] splitString = dateTime.split(" ");
        String messageTime = splitString[1];


        if(fromUserId.equals(currentUserId))
        {

            if(message.getMessageType().equals(Constants.MESSAGE_TYPE_TEXT))
            {
                holder.llSent.setVisibility(View.VISIBLE);
                holder.llSentImage.setVisibility(View.GONE);
            }
            else
            {
                holder.llSent.setVisibility(View.GONE);
                holder.llSentImage.setVisibility(View.VISIBLE);

            }

            holder.llReceived.setVisibility(View.GONE);
            holder.llReceivedImage.setVisibility(View.GONE);



            holder.tvSentMessage.setText(message.getMessage());
            holder.tvSentMessageTime.setText(messageTime);
            holder.tvSentImageTime.setText(messageTime);
            Glide.with(context)
                    .load(message.getMessage())
                    .placeholder(R.drawable.ic_baseline_sms_failed_24)
                    .into(holder.ivSent);

        }
        else
        {
            if(message.getMessageType().equals(Constants.MESSAGE_TYPE_TEXT))
            {
                holder.llReceived.setVisibility(View.VISIBLE);
                holder.llReceivedImage.setVisibility(View.GONE);
            }
            else
            {
                holder.llReceived.setVisibility(View.GONE);
                holder.llReceivedImage.setVisibility(View.VISIBLE);

            }

            holder.llSent.setVisibility(View.GONE);
            holder.llSent.setVisibility(View.GONE);



            holder.tvReceivedMessage.setText(message.getMessage());
            holder.tvReceivedMessageTime.setText(messageTime);
            holder.tvReceivedImageTime.setText(messageTime);
            Glide.with(context)
                    .load(message.getMessage())
                    .placeholder(R.drawable.ic_baseline_sms_failed_24)
                    .into(holder.ivReceived);

        }

        holder.clMessage.setTag(R.id.TAG_MESSAGE,message.getMessage());
        holder.clMessage.setTag(R.id.TAG_MESSAGE_ID,message.getMessageId());
        holder.clMessage.setTag(R.id.TAG_MESSAGE_TYPE,message.getMessageType());

        holder.clMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageType = view.getTag(R.id.TAG_MESSAGE_TYPE).toString();
                Uri uri = Uri.parse(view.getTag(R.id.TAG_MESSAGE_TYPE).toString());
                if(messageType.equals(Constants.MESSAGE_TYPE_VIDEO))
                {
                   Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                   intent.setDataAndType(uri,"video/mp4");
                   context.startActivity(intent);
                }
                else if(messageType.equals(Constants.MESSAGE_TYPE_IMAGE))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setDataAndType(uri,"image/jpg");
                    context.startActivity(intent);
                }

            }
        });

        holder.clMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (actionMode != null) {
                    return false;
                }
                selectedView = holder.clMessage;
                actionMode =((AppCompatActivity)context).startSupportActionMode(actionModeCallback);
                holder.clMessage.setBackgroundColor(context.getResources().getColor(R.color.action_mode_background));
                return true ;
            }
        });

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llSent, llReceived, llSentImage, llReceivedImage;
        private TextView tvSentMessage, tvSentMessageTime, tvReceivedMessage, tvReceivedMessageTime, tvSentImageTime, tvReceivedImageTime;
        private ConstraintLayout clMessage;
        private ImageView ivSent, ivReceived;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            llSent = itemView.findViewById(R.id.llSent);
            llReceived = itemView.findViewById(R.id.llReceived);
            tvSentMessage = itemView.findViewById(R.id.tvSentMessage);
            tvSentMessageTime = itemView.findViewById(R.id.tvSentMessageTime);

            tvReceivedMessage = itemView.findViewById(R.id.tvReceivedMessage);
            tvReceivedMessageTime = itemView.findViewById(R.id.tvReceivedMessageTime);

            clMessage = itemView.findViewById(R.id.clMessage);

            llSentImage = itemView.findViewById(R.id.llSentImage);
            llReceivedImage = itemView.findViewById(R.id.llReceivedImage);
            ivSent = itemView.findViewById(R.id.ivSent);
            ivReceived = itemView.findViewById(R.id.ivReceived);

            tvSentImageTime = itemView.findViewById(R.id.tvSentImageTime);
            tvReceivedImageTime = itemView.findViewById(R.id.tvReceivedImageTime);


        }
    }

        public ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.menu_chat_options,menu);

                String selectedMessageType = String.valueOf(selectedView.getTag(R.id.TAG_MESSAGE_TYPE));
                if(selectedMessageType.equals(Constants.MESSAGE_TYPE_TEXT))
                {
                    MenuItem download = menu.findItem(R.id.mnuDownload);
                    download.setVisible(false);
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                String selectedMessageId = selectedView.getTag(R.id.TAG_MESSAGE_ID).toString();
                String selectedMessage = selectedView.getTag(R.id.TAG_MESSAGE).toString();
                String selectedMessageType = selectedView.getTag(R.id.TAG_MESSAGE_TYPE).toString();

                switch(itemId)
                {
                    case R.id.mnuDelete:

                        if(context instanceof chat_activity)
                        {
                            ((chat_activity)context).deleteMessage(selectedMessageId,selectedMessageType);
                        }

                        actionMode.finish();
                        break;
                    case R.id.mnuDownload:
                        if(context instanceof chat_activity)
                        {
                            ((chat_activity)context).downloadMessage(selectedMessageId,selectedMessageType,false);
                        }

                        actionMode.finish();
                        break;

                    case R.id.mnuShare:
                        if(selectedMessageType.equals(Constants.MESSAGE_TYPE_TEXT))
                        {
                            Intent intentShare = new Intent();
                            intentShare.setAction(Intent.ACTION_SEND);
                            intentShare.putExtra(Intent.EXTRA_TEXT,selectedMessage);
                            intentShare.setType("text/plain");
                            context.startActivity(intentShare);
                        }
                        else
                        {
                            ((chat_activity)context).downloadMessage(selectedMessageId,selectedMessageType,true);

                        }
                        actionMode.finish();
                        break;
                    case R.id.mnuForward:
                        if(context instanceof chat_activity)
                        {
                            ((chat_activity)context).forwardMessage(selectedMessageId,selectedMessage,selectedMessageType);
                        }
                        actionMode.finish();
                        break;

                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

                actionMode= null;
                selectedView.setBackgroundColor(context.getResources().getColor(R.color.light_blue_2));
            }
        };
    }

