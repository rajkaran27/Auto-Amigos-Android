package com.example.autoamigos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.checkout.approve.*;
import com.paypal.checkout.createorder.*;
import com.paypal.checkout.order.*;
import com.paypal.checkout.paymentbutton.PaymentButtonContainer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;


public class Payment extends AppCompatActivity {

    PaymentButtonContainer paymentButtonContainer;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paypal_payment);

        Intent intent = getIntent();
        int carPriceInteger = intent.getIntExtra("car_price", 0);
        String name = intent.getStringExtra("name");
        String address = intent.getStringExtra("address");
        String phone = intent.getStringExtra("phone");
        int carId = getIntent().getIntExtra("car_id", 0);
        double carPrice = (double) carPriceInteger;
        paymentButtonContainer = findViewById(R.id.payment_button_container);

        paymentButtonContainer.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.SGD)
//                                                        .value(Price)
                                                        .value(String.format("%.2f", carPrice))
                                                        .build()

                                        )
                                        .build()
                        );
                        OrderRequest order = new OrderRequest(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));

                                AddOrdertoDB(name, address, phone, carId, carPrice);

                                Toast.makeText(Payment.this, "Success!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Payment.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }
        );
    }

    private void AddOrdertoDB(String name, String address, String phone, int carId, Double price) {
        HashMap<String, Object> orderHashMap = new HashMap<>();
        orderHashMap.put("name", name);
        orderHashMap.put("address", address);
        orderHashMap.put("phone", phone);
        orderHashMap.put("carid", carId);
        orderHashMap.put("price", price);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        String key = ordersRef.push().getKey(); // Generate a unique key for the order
        orderHashMap.put("key", key);

        ordersRef.child(key).setValue(orderHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Payment.this, "", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
