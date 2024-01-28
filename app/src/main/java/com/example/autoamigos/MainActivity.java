package com.example.autoamigos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            ImageButton imageButton = (ImageButton) findViewById(R.id.btnToLogin);
            Glide.with(this).load(currentUser.getPhotoUrl()).into(imageButton);
        }
        getCars();
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnToLogin) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }else if(v.getId()==R.id.btnExplore){
            Intent i = new Intent(this,AllCarsActivity.class);
            startActivity(i);
        }
    }

    public void getCars(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("cars");
        List<Car> carList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                carList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Car car = postSnapshot.getValue(Car.class);
                    carList.add(car);
                }
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                CarAdapter adapter = new CarAdapter(getApplicationContext(), carList, car -> {
                    Intent intent = new Intent(MainActivity.this, CarDetailActivity.class);
                    intent.putExtra("car_id", car.getCar_id()); // Assuming each car has an ID or some identifier
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        })



        ;

    }


}