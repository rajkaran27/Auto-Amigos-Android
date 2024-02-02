package com.example.autoamigos;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.UploadTask;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText txtName;

  private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private static final String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();


        txtName = findViewById(R.id.display_name);
        TextView btnSave = findViewById(R.id.doneText);
        btnSave.setOnClickListener(this);

        ImageView back_button = (ImageView) findViewById(R.id.btnBack);
        back_button.setOnClickListener(this);

        ImageView imageView = (ImageView) findViewById(R.id.profile_pic);
        Glide.with(this).load(currentUser.getPhotoUrl()).into(imageView);

        // display name
        String displayName = currentUser.getDisplayName();
        EditText txtName = findViewById(R.id.display_name);
        txtName.setText(displayName);

        //phone number
        String phoneNumber = currentUser.getPhoneNumber();
        Log.d("PhoneNumber", "Phone Number: " + phoneNumber);
        TextView txtNumber = findViewById(R.id.mobile_number);
        if (txtNumber != null){
            txtNumber.setText("Not Available");
        }else {
            txtNumber.setText(phoneNumber);
        }

        //email
        String currentUserEmailemail = currentUser.getEmail();
        TextView email = findViewById(R.id.email);
        email.setText(currentUserEmailemail);

        // camera
        ImageView profilePicImageView = (ImageView) findViewById(R.id.profile_pic);
        profilePicImageView.setOnClickListener(this);
        TextView changePicTextView = findViewById(R.id.change_pic);
        changePicTextView.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnLogout){
            logout();
        } else if(v.getId() == R.id.doneText){
            updateDisplayName();
        }else if (v.getId() == R.id.btnBack){
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            startActivity(profileIntent);
        } else if (v.getId() == R.id.profile_pic) {
            openCamera();
        } else if (v.getId() == R.id.change_pic){
            openGallery();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            uploadImageAndSaveUri(imageBitmap);
        } else if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                uploadImageAndSaveUri(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageAndSaveUri(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("pics/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        // Upload the image to Firebase Storage
        storageRef.putBytes(imageBytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // Get the download URL for the uploaded image
                    storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> uriTask) {
                            if (uriTask.isSuccessful()) {
                                Uri downloadUri = uriTask.getResult();

                                // Update the Firebase user profile with the new photo URL
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(downloadUri)
                                        .build();

                                currentUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // User profile updated successfully
                                            Intent profileIntent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                            startActivity(profileIntent);
                                            Toast.makeText(EditProfileActivity.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Handle the error
                                            Toast.makeText(EditProfileActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // Handle the error
                                Toast.makeText(EditProfileActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Handle the error
                    Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateDisplayName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String newDisplayName = txtName.getText().toString();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .build();
        currentUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // User display name updated successfully
                    Intent profileIntent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                    Toast.makeText(EditProfileActivity.this, "Display Name Updated", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the error
                    Toast.makeText(EditProfileActivity.this, "Failed to update display name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void logout(){
        mAuth.signOut();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish(); // Close the current activity
    }
}
