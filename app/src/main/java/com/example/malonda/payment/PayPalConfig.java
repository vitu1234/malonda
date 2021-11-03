package com.example.malonda.payment;

import android.app.Application;
import android.content.Context;

import com.example.malonda.BuildConfig;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

public class PayPalConfig {
    String YOUR_CLIENT_ID = "AXcHk9EXM51f_5oWbn4FGIw2a1EPCgJ-RzAO_sWLKk5HqFns-Gcl8BLsLTXpy7mTiF75Tz3PFA8v2mMT";
    Context context;
    public PayPalConfig() {
    }

    public PayPalConfig(Context context) {
        this.context = context;
    }

    public void configurePayPal(){
        CheckoutConfig config = new CheckoutConfig(
                (Application) context.getApplicationContext(),
                YOUR_CLIENT_ID,
                Environment.SANDBOX,
                String.format("com.example.malonda://paypalpay", BuildConfig.APPLICATION_ID),
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                new SettingsConfig(
                        true,
                        false
                )
        );
        PayPalCheckout.setConfig(config);
    }
}