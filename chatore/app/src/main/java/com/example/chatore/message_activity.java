package com.example.chatore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.util.Util;

public class message_activity extends AppCompatActivity {

    private TextView tvMessage ;
    private ProgressBar pbMessage ;
    private ConnectivityManager.NetworkCallback networkCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        tvMessage =findViewById(R.id.tvMessage);
        pbMessage = findViewById(R.id.pbMessage);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP)
        {
            networkCallback = new ConnectivityManager.NetworkCallback()
            {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    finish();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    tvMessage.setText(R.string.no_internet_connection);
                }
            };

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build(),networkCallback);
        }


    }

    public void btnRetryClick(View v)
    {
        pbMessage.setVisibility(View.VISIBLE);
        if(util.connectionAvailable(this))
        {
            finish();
        }
        else
        {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            },1000);
        }


    }

    public void btnCloseClick(View view)
    {
        finishAffinity();

    }
}