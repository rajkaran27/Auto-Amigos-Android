package com.example.autoamigos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

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

        ImageView imageView = (ImageView) findViewById(R.id.profile_pic);

        Glide.with(this).load(currentUser.getPhotoUrl()).into(imageView);

    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnLogout){
            logout();
        }
    }

    private void logout(){
        mAuth.signOut();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish(); // Close the current activity
    }
}
