package com.example.chatore;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class util {

    public static boolean connectionAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null && connectivityManager.getActiveNetworkInfo()!=null)
        {
            return connectivityManager.getActiveNetworkInfo().isAvailable() ;

        }
        else
        {
            return false;
        }
    }


    public static void updateDeviceToken(Context context ,String token) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference databaseReference = rootRef.child(nodenames.TOKENS).child(currentUser.getUid());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(nodenames.DEVICE_TOKEN, token);

            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (!task.isSuccessful()) {
                        Toast.makeText(context, R.string.failed_add_device_token, Toast.LENGTH_SHORT).show();
                    }

                }

            });

        }
    }

    public static void sendNotification(Context context ,String title,String message,String userId)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference = rootRef.child(nodenames.TOKENS).child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(nodenames.DEVICE_TOKEN).getValue()!=null)
                {
                    String deviceToken = snapshot.child(nodenames.DEVICE_TOKEN).getValue().toString();
                    JSONObject notification = new JSONObject();
                    JSONObject notificationData = new JSONObject();

                    try {
                        notificationData.put(Constants.NOTIFICATION_TITLE,title);
                        notificationData.put(Constants.NOTIFICATION_MESSAGE,message);

                        notification.put(Constants.NOTIFICATION_T0,deviceToken);
                        notification.put(Constants.NOTIFICATION_DATA,notificationData);

                        String fcmApiUrl = "https://fcm.googleapis.com/fcm/send";
                        String contentType = "application/json";

                        Response.Listener successListener = new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {

                                Toast.makeText(context, R.string.notification_sent, Toast.LENGTH_SHORT).show();
                            }
                        };

                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, context.getString( R.string.failed_to_send_notification,error.getMessage()), Toast.LENGTH_SHORT).show();
                            }
                        };

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(fcmApiUrl,notification,successListener,errorListener)
                        {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String,String> params = new HashMap<>();
                                params.put("Authorization","key="+Constants.FIREBASE_KEY);
                                params.put("Sender","id="+Constants.SENDER_ID);
                                params.put("Content-Type",contentType);
                                return params;

                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(jsonObjectRequest);




                    } catch (JSONException e) {
                        Toast.makeText(context, context.getString( R.string.failed_to_send_notification,e.getMessage()), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, context.getString( R.string.failed_to_send_notification,error.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
