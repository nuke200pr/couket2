package com.example.chatore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changePassword extends AppCompatActivity {

    private TextInputEditText etPassword , etConfirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etPassword = findViewById(R.id.etPassword4);
        etConfirmPassword = findViewById(R.id.confirmPassword4);
    }


    public void passwordChangeButton(View view)
    {
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if(password.isEmpty())
        {
            etPassword.setError(getString(R.string.enter_password));

        }
        else if(confirmPassword.isEmpty())
        {
            etConfirmPassword.setError(getString(R.string.confirm_password));
        }
        else if (!password.equals(confirmPassword))
        {
            etPassword.setError(getString(R.string.confirm_password_not_equal_to_password));
        }
        else
        {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser =firebaseAuth.getCurrentUser();

            if(firebaseUser!=null)
            {
                firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(changePassword.this, R.string.password_update_success, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(changePassword.this ,loginactivity.class));
                        }
                        else
                        {
                            Toast.makeText(changePassword.this, getString(R.string.password_change_failed,task.getException()), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
    }



}