package com.example.chatore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etName2 ,etEmail2;
    private String name ,email ;
    private FirebaseUser firebaseuser ;
    private DatabaseReference databaseReference;

    private StorageReference filestorage;
    private Uri localfileuri ,serverfileuri;
    private ImageView ivProfile;

    private FirebaseAuth fireBaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etEmail2 = findViewById(R.id.etEmail3);
        etName2 = findViewById(R.id.etName3);


        filestorage = FirebaseStorage.getInstance().getReference();
        ivProfile = findViewById(R.id.imageView3);

        fireBaseAuth = FirebaseAuth.getInstance();
        firebaseuser = fireBaseAuth.getCurrentUser();

        if (firebaseuser != null) {
            etName2.setText(firebaseuser.getDisplayName());
            etEmail2.setText(firebaseuser.getEmail());
            serverfileuri = firebaseuser.getPhotoUrl();

            if (serverfileuri != null) {
                Glide.with(this)
                        .load(serverfileuri).placeholder(R.drawable.loading)
                        .error(R.drawable.profilepic)
                        .into(ivProfile);

            }
        }

    }




    public void changeImage(View view) {

        if (serverfileuri == null) {
            pickImage();
        } else {


            PopupMenu popupMenu = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.menu_picture, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    int id = menuItem.getItemId();
                    if (id == R.id.mnuChangePic) {
                        pickImage();
                    } else if (id == R.id.mnuRemovePic) {
                        removePhoto();
                    }
                    return false;
                }
            });
            popupMenu.show();


        }
    }



    public void btnLogoutClick(View view)
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        startActivity(new Intent(ProfileActivity.this,loginactivity.class));
        finishAffinity();
    }


    public void btnSaveClick(View view)
    {
        if(etName2.getText().toString().trim().isEmpty())
        {
            etName2.setError(getString(R.string.enter_name));

        }
        else
        {
            if(localfileuri!=null)
            {
                updateNameandPhoto();
            }
            else
            {
                updateOnlyName();
            }
        }
    }



    private void pickImage()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},102);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==102)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            }
            else
            {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101) {
            if (resultCode == RESULT_OK)
            {
                localfileuri = data.getData();

                ivProfile.setImageURI(localfileuri);
            }
        }
    }




    private void removePhoto()
    {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName2.getText().toString().trim())
                .setPhotoUri(null)
                .build();


        firebaseuser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    String userID = firebaseuser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(nodenames.USERS);

                    HashMap<String, String> hashMap = new HashMap<>();


                    hashMap.put(nodenames.PHOTO,"");

                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(ProfileActivity.this, "Photo Removed Successfully", Toast.LENGTH_SHORT).show();
                            ivProfile.setImageResource(R.drawable.profilepic);


                        }

                    });

                }
                else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
                }
    }




    });
}



    private void updateNameandPhoto() {
        String strFileName = firebaseuser.getUid() + ".jpg";
        final StorageReference fileRef = filestorage.child("images/" + strFileName);
        fileRef.putFile(localfileuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            serverfileuri = uri;
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etName2.getText().toString().trim())
                                    .setPhotoUri(serverfileuri)
                                    .build();

                            firebaseuser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        String userID = firebaseuser.getUid();
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child(nodenames.USERS);

                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put(nodenames.NAME, etName2.getText().toString().trim());

                                        hashMap.put(nodenames.PHOTO, serverfileuri.getPath());

                                        databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();


                                            }
                                        });

                                    } else {
                                        Toast.makeText(ProfileActivity.this, getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    });
                }
            }
        });
    }




    public void updateOnlyName ()
    {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName2.getText().toString().trim())
                .build();
        firebaseuser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    String userID = firebaseuser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(nodenames.USERS);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(nodenames.NAME, etName2.getText().toString().trim());


                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();


                        }
                    });

                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void btnChangePassword(View view)
    {
        startActivity(new Intent(ProfileActivity.this ,changePassword.class));
    }
}