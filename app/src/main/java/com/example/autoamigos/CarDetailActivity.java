package com.example.autoamigos;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CarDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        Intent intent = getIntent();

        if (intent.hasExtra("car_id")) {
            // Retrieve the car ID from the intent
            int carId = getIntent().getIntExtra("car_id", 0);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("cars");

            databaseReference.orderByKey().equalTo(String.valueOf(carId))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Assuming each carId has a unique entry and your car object has a proper constructor and getters
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    Car car = child.getValue(Car.class);
                                    ImageView imageViewCar = findViewById(R.id.imgCar);
                                    TextView txtModel = findViewById(R.id.txtModel);
                                    TextView txtDesc = findViewById(R.id.txtDesc);
                                    TextView txtCat = findViewById(R.id.txtCat);
                                    TextView txtCap = findViewById(R.id.txtCap);
                                    TextView txtTrans = findViewById(R.id.txtTrans);
                                    TextView txtPrice = findViewById(R.id.txtPrice);
                                    Button btnBuy = findViewById(R.id.btnBuy);

                                    txtModel.setText(car.getModel());
                                    txtDesc.setText(car.getDescription());
                                    txtCat.setText(car.getCategory_name());
                                    txtCap.setText(car.getCapacity());
                                    txtTrans.setText(car.getTransmission());
                                    txtPrice.setText("$"+car.getPrice());
                                    Glide.with(CarDetailActivity.this)
                                            .load(car.getImage_url())
                                            .into(imageViewCar);
                                    btnBuy.setOnClickListener(v -> {
                                        Intent purchaseIntent = new Intent(CarDetailActivity.this, PurchaseActivity.class);
                                        purchaseIntent.putExtra("car_id", carId);
                                        startActivity(purchaseIntent);
                                    });


                                }
                            } else {
                                // Handle the case where the car with the given id does not exist
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "Failed to read value.", databaseError.toException());
                        }
                    });

        } else {
            Log.e("CarDetailActivity", "No car ID provided in the intent");
        }
    }
}
