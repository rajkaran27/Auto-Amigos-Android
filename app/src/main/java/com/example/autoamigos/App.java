package com.example.autoamigos;

import android.app.Application;

import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PayPalCheckout.setConfig(new CheckoutConfig(
                this,
                "AVj8LVRBvmJikm-8YTlV9SWmsqB0hQV0EC1eVGLSBkFDpASqz6fIJnut7HUblaNTo351Iux5t86WIiaZ",
                Environment.SANDBOX,
                CurrencyCode.SGD,
                UserAction.PAY_NOW,
                "com.example.autoamigos://paypalpay"
        ));
    }
}
