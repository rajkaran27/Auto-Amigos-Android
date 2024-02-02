package com.example.autoamigos;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.*;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

public class PurchaseActivity extends AppCompatActivity {
    private TextInputEditText txtName, txtAddress, txtPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        txtName = findViewById(R.id.txtName);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhone = findViewById(R.id.txtNum);

        setContentView(R.layout.activity_payment);
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
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    Car car = child.getValue(Car.class);
                                    ImageView imageViewCar = findViewById(R.id.imageView6);
                                    TextView txtModel = findViewById(R.id.textViewInsideContainer2);

                                    TextView txtPrice = findViewById(R.id.txtSubtotal);
                                    TextView txtPrice2 = findViewById(R.id.txttotal);
                                    TextView txtTax = findViewById(R.id.txtGST);
                                    Button btnBuy = findViewById(R.id.btnBuyCar);

                                    txtName = findViewById(R.id.txtName);
                                    txtAddress = findViewById(R.id.txtAddress);
                                    txtPhone = findViewById(R.id.txtNum);

                                    txtModel.setText(car.getModel());
                                    txtPrice.setText("$"+car.getPrice());
                                    txtTax.setText("$"+car.getTax());
                                    txtPrice2.setText("$"+car.getPrice());

                                    Glide.with(PurchaseActivity.this)
                                            .load(car.getImage_url())
                                            .into(imageViewCar);
                                    btnBuy.setOnClickListener(v -> {
                                            // All fields are filled, proceed to the payment page
                                        String name = txtName.getText().toString();
                                        String address = txtAddress.getText().toString();
                                        String phone = txtPhone.getText().toString();

                                        // Check if any of the fields is empty
                                        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                                            // Display a message to the user indicating that all fields are required
                                            Toast.makeText(PurchaseActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // All fields are filled, proceed to the payment page
                                            Intent purchaseIntent = new Intent(PurchaseActivity.this, Payment.class);
                                            purchaseIntent.putExtra("car_price", car.getPrice());
                                            purchaseIntent.putExtra("name", name);
                                            purchaseIntent.putExtra("address", address);
                                            purchaseIntent.putExtra("phone", phone);
                                            purchaseIntent.putExtra("car_id", carId);
                                            startActivity(purchaseIntent);
                                        }
                                    });


                                }
                            } else {
                                Intent mainIntent = new Intent(PurchaseActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "Failed to read value.", databaseError.toException());
                        }
                    });

        } else {
            Log.e("PurchaseActivity", "No car ID provided in the intent!");
        }
    }
}
