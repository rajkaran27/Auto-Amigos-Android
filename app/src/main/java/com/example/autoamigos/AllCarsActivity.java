package com.example.autoamigos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllCarsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_cars_activity);
        getCars();
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
                recyclerView.setLayoutManager(new LinearLayoutManager(AllCarsActivity.this, LinearLayoutManager.VERTICAL, false));
                AllCarAdapter adapter = new AllCarAdapter(getApplicationContext(), carList, car -> {
                    Intent intent = new Intent(AllCarsActivity.this, CarDetailActivity.class);
                    intent.putExtra("car_id", car.getCar_id());
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

}
