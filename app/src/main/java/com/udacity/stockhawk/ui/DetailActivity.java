package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.udacity.stockhawk.R;

public class DetailActivity extends AppCompatActivity {

    TextView mStockName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mStockName = (TextView) findViewById(R.id.tv_stock_name);

        Intent intentThatStartedThisActivity = getIntent();

        // If the activity was called with a card object as extra
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("stock_name")) {
                final String symbol = intentThatStartedThisActivity.getStringExtra("stock_name");
                mStockName.setText(symbol);
            }
        }
    }
}
