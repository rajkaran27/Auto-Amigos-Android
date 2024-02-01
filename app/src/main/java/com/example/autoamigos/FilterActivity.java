package com.example.autoamigos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterActivity extends AppCompatActivity {
    private RadioGroup categoryRadioGroup;
    private RadioGroup capacityRadioGroup;
    private RadioGroup brandRadioGroup;

    private CheckBox cap2CheckBox;
    private CheckBox cap4CheckBox;
    private CheckBox cap5CheckBox;
    private CheckBox cap7CheckBox;
    private DatabaseReference databaseReference;
    private Set<String> selectedCapacitiesSet = new HashSet<>();
    private StringBuilder selectedCapacities = new StringBuilder();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);
        databaseReference = FirebaseDatabase.getInstance().getReference("cars");

        categoryRadioGroup = findViewById(R.id.Category);
        brandRadioGroup = findViewById(R.id.Brand);

        cap2CheckBox = findViewById(R.id.cap2);
        cap4CheckBox = findViewById(R.id.cap4);
        cap5CheckBox = findViewById(R.id.cap5);
        cap7CheckBox = findViewById(R.id.cap7);

        Button btnFilterRes = findViewById(R.id.btnFilterRes);
        btnFilterRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryFirebaseDatabase();
            }
        });

        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilters();
            }
        });
    }

    private void queryFirebaseDatabase() {
        // Retrieve selected filters
        String selectedCategory = getSelectedCategory();
        String selectedBrand = getSelectedBrand();
        String selectedCapacitiesString = selectedCapacities.toString().trim();

        // Initialize the base query
        Query baseQuery = databaseReference;

        // Add filters based on user selection
        if (!selectedCategory.isEmpty()) {
            baseQuery = baseQuery.orderByChild("category_name").equalTo(selectedCategory);
        }

        if (!selectedBrand.isEmpty()) {
            baseQuery = baseQuery.orderByChild("brand_name").equalTo(selectedBrand);
        }

        if (!selectedCapacitiesSet.isEmpty()) {
            String firstCapacity = selectedCapacitiesSet.iterator().next();
            baseQuery = baseQuery.orderByChild("capacity").equalTo(firstCapacity);
        }

        baseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Car> carList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Car car = snapshot.getValue(Car.class);
                    carList.add(car);
                    Log.d("carmodel", "name" + car.getModel());
                }
                Intent intent = new Intent(FilterActivity.this, AllCarsActivity.class);
                // Put the filtered car list as an extra using Serializable
                intent.putExtra("filtered_car_list", (Serializable) carList);
                // Start the AllCarsActivity
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error querying database", databaseError.toException());
            }
        });
    }


    private String getSelectedCategory() {
        int selectedCategoryId = categoryRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedCategoryRadioButton = findViewById(selectedCategoryId);
        return (selectedCategoryRadioButton != null) ? selectedCategoryRadioButton.getTag().toString() : "";
    }

    private String getSelectedBrand() {
        int selectedBrandId = brandRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedBrandRadioButton = findViewById(selectedBrandId);
        return (selectedBrandRadioButton != null) ? selectedBrandRadioButton.getTag().toString() : "";
    }
    private void clearFilters() {
        categoryRadioGroup.clearCheck();
        brandRadioGroup.clearCheck();

        if (cap2CheckBox.isChecked()) {
            cap2CheckBox.setChecked(false);
        }

        if (cap4CheckBox.isChecked()) {
            cap4CheckBox.setChecked(false);
        }

        if (cap5CheckBox.isChecked()) {
            cap5CheckBox.setChecked(false);
        }

        if (cap7CheckBox.isChecked()) {
            cap7CheckBox.setChecked(false);
        }

        selectedCapacitiesSet.clear();
        selectedCapacities.setLength(0);
    }
    private void updateCapacitySelection(CompoundButton buttonView, boolean isChecked, String capacity) {
        if (isChecked) {
            selectedCapacities.append(capacity).append(" ");
        } else {
            selectedCapacities.replace(selectedCapacities.indexOf(capacity), selectedCapacities.indexOf(capacity) + capacity.length(), "");
        }
        Log.d("Capacities", "Selected Capacities: " + selectedCapacities.toString());
    }

}
