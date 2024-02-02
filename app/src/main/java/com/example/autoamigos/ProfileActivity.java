package com.example.autoamigos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;




public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        Button btnEditProfile = findViewById(R.id.edit_profile);
        btnEditProfile.setOnClickListener(this);

        ImageView back_button = (ImageView) findViewById(R.id.btnBack);
        back_button.setOnClickListener(this);

        ImageView imageView = (ImageView) findViewById(R.id.profile_pic);
        Glide.with(this).load(currentUser.getPhotoUrl()).into(imageView);

        // display name
        String displayName = currentUser.getDisplayName();
        TextView txtName = findViewById(R.id.name);
        txtName.setText(displayName);

        //phone number
        String phoneNumber = currentUser.getPhoneNumber();
        TextView txtNumber = findViewById(R.id.number);
        txtNumber.setText(phoneNumber);

    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnLogout){
            showLogoutConfirmationDialog();
//            logout();
        } else if (v.getId() == R.id.edit_profile){
            Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
            startActivity(editProfileIntent);
        } else if (v.getId() == R.id.btnBack){
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
        }
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(i);
                        Toast.makeText(ProfileActivity.this, "Log Out Successful", Toast.LENGTH_SHORT).show();
                        finish(); // Close the current activity
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
    private void logout(){
        mAuth.signOut();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        Toast.makeText(ProfileActivity.this, "Log Out Su", Toast.LENGTH_SHORT).show();
        finish(); // Close the current activity
    }
}
