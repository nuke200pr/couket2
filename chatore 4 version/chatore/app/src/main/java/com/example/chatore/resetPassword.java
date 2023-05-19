package com.example.chatore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class resetPassword extends AppCompatActivity {

    private TextInputEditText etEmail ;
    private TextView tvMessage;
    private LinearLayout llResetPassword ,llMessage;
    private Button btnRetry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etEmail = findViewById(R.id.etEmail5);
        tvMessage = findViewById(R.id.messageMail);
        llResetPassword = findViewById(R.id.llResetPassword);
        llMessage = findViewById(R.id.llMessage);

        btnRetry = findViewById(R.id.btnRetry);



    }

    public void resetPasswordbtn(View view)
    {
        String email = etEmail.getText().toString().trim();
        if(email.isEmpty())
        {
            etEmail.setError(getString(R.string.enter_email));
        }
        else
        {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    llResetPassword.setVisibility(View.GONE);
                    llMessage.setVisibility(View.VISIBLE);

                    if(task.isSuccessful())
                    {
                        tvMessage.setText(getString(R.string.reset_password_instructions,email));
                        new CountDownTimer(60000,1000){

                            @Override
                            public void onTick(long l) {
                            btnRetry.setText(getString(R.string.resend_timer,String.valueOf(l/1000)));
                            btnRetry.setOnClickListener(null);
                            }

                            @Override
                            public void onFinish() {
                                btnRetry.setText(R.string.retry_mail  );

                                btnRetry.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        llResetPassword.setVisibility(View.VISIBLE);
                                        llMessage.setVisibility(View.GONE);
                                    }
                                });

                            }
                        }.start();
                    }
                    else
                    {
                        tvMessage.setText(getString(R.string.sending_email_failed,task.getException()));
                        btnRetry.setText(R.string.retry_mail  );

                        btnRetry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                llResetPassword.setVisibility(View.VISIBLE);
                                llMessage.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }
    }

    public void buttonCloseClick(View view)
    {
        finish();
    }
}