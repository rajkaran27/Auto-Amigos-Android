package com.example.autoamigos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.paypal.checkout.approve.*;
import com.paypal.checkout.createorder.*;
import com.paypal.checkout.order.*;
import com.paypal.checkout.paymentbutton.PaymentButtonContainer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class Payment extends AppCompatActivity {
    PaymentButtonContainer paymentButtonContainer;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paypal_payment);
        Intent intent = getIntent();
        int carPriceInteger = intent.getIntExtra("car_price", 0); // Retrieve as int
        double carPrice = (double) carPriceInteger;
        System.out.println(intent != null && intent.hasExtra("car_price"));
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
                                Toast.makeText(Payment.this, "Success!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Payment.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }
        );
    }
}
