package com.example.autoamigos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllCarsActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Car> carList = new ArrayList<>();
    private AllCarAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_cars_activity);
        TextView noResultsText = findViewById(R.id.txtNoResults);
        noResultsText.setVisibility(View.GONE);
        setupSearchView();
        getCars();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.btnFilter){
            Intent intent = new Intent(this, FilterActivity.class);
            startActivity(intent);
        }
    }


    private void setupSearchView() {
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getSearchCars(newText);
                return true;
            }
        });
    }


    private void getSearchCars (String query){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference carsRef = databaseReference.child("cars");

        Query searchQuery = carsRef.orderByChild("model").startAt(query).endAt(query + "\uf8ff");
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                carList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Car car = snapshot.getValue(Car.class);
                    carList.add(car);
                }
                TextView noResultsText = findViewById(R.id.txtNoResults);
                if (carList.isEmpty()) {
                    noResultsText.setVisibility(View.VISIBLE);
                }else if(adapter != null) {
                    noResultsText.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String errorMessage = "Database Error: " + databaseError.getMessage();
                Toast.makeText(AllCarsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("Firebase Database Error", errorMessage);            }
        });
    }


    public void getCars() {
        if (getIntent().hasExtra("filtered_car_list")) {
            carList = (List<Car>) getIntent().getSerializableExtra("filtered_car_list");
            setupRecyclerView(carList);
        } else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("cars");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    carList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Car car = postSnapshot.getValue(Car.class);
                        carList.add(car);
                    }
                    setupRecyclerView(carList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors.
                }
            });
        }
    }

    private void setupRecyclerView(List<Car> carList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllCarsActivity.this, LinearLayoutManager.VERTICAL, false));
        adapter = new AllCarAdapter(getApplicationContext(), carList, car -> {
            Intent intent = new Intent(AllCarsActivity.this, CarDetailActivity.class);
            intent.putExtra("car_id", car.getCar_id());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

}
