package com.bruce.raeasy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.bruce.raeasy.R;

import java.util.Objects;

public class PaymentActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button btnMpesa, btnAirtel, btnComplete;
    private TextView txtAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initViews();

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Payment");
    }

    private void initViews() {
        mToolbar = findViewById(R.id.payment_toolbar);
        btnMpesa = findViewById(R.id.mpesaBtn);
        btnAirtel = findViewById(R.id.airtelBtn);
        btnComplete = findViewById(R.id.btnComplete);
        txtAmount = findViewById(R.id.txtAmount);
    }
}
