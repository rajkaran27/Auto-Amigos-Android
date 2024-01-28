package com.example.autoamigos;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button googleAuth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN=20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        googleAuth = findViewById(R.id.btnLogin);

        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //ignore this error
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        googleAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
    }

    private void googleSignIn(){
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==RC_SIGN_IN){
    //   Task<GoogleSignInAccount> task = GoogleSignIn.getLastSignedInAccountFromIntent(data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuth(account.getIdToken());
                } else {
                    Toast.makeText(this, "GoogleSignInAccount is null", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void firebaseAuth(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("id",user.getUid());
                            map.put("name",user.getDisplayName());
                            map.put("profile",user.getPhotoUrl().toString());

                            database.getReference().child("users").child(user.getUid()).setValue(map);

                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginActivity.this,"wrong",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}