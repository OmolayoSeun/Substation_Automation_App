package com.cyril.substationautomationapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    public static final String FEEDER_1_MAX_VOLTS = "FEEDER_1_MAX_VOLTS";
    public static final String FEEDER_1_MAX_CURRENT = "FEEDER_1_MAX_CURRENT";
    public static final String FEEDER_1_MAX_TEMP = "FEEDER_1_MAX_TEMP";
    public static final String FEEDER_2_MAX_VOLTS = "FEEDER_2_MAX_VOLTS";
    public static final String FEEDER_2_MAX_CURRENT = "FEEDER_2_MAX_CURRENT";
    public static final String FEEDER_2_MAX_TEMP = "FEEDER_2_MAX_TEMP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setTitle("Threshold Setting");

        EditText f1_t_v = findViewById(R.id.f1_t_v);
        EditText f1_t_c = findViewById(R.id.f1_t_c);
        EditText f1_t_t = findViewById(R.id.f1_t_t);
        EditText f2_t_v = findViewById(R.id.f2_t_v);
        EditText f2_t_c = findViewById(R.id.f2_t_c);
        EditText f2_t_t = findViewById(R.id.f2_t_t);
        Button save_btn = findViewById(R.id.save_btn);


        Intent intent = getIntent();

        if (intent != null) {

            f1_t_v.setText(String.valueOf(intent.getFloatExtra(FEEDER_1_MAX_VOLTS, 0f)));
            f1_t_c.setText(String.valueOf(intent.getFloatExtra(FEEDER_1_MAX_CURRENT, 0f)));
            f1_t_t.setText(String.valueOf(intent.getFloatExtra(FEEDER_1_MAX_TEMP, 0f)));
            f2_t_v.setText(String.valueOf(intent.getFloatExtra(FEEDER_2_MAX_VOLTS, 0f)));
            f2_t_c.setText(String.valueOf(intent.getFloatExtra(FEEDER_2_MAX_CURRENT, 0f)));
            f2_t_t.setText(String.valueOf(intent.getFloatExtra(FEEDER_2_MAX_TEMP, 0f)));
        }


        save_btn.setOnClickListener(v -> {

            Intent resultIntent = new Intent();
            resultIntent.putExtra(FEEDER_1_MAX_VOLTS, Float.parseFloat(f1_t_v.getText().toString()));
            resultIntent.putExtra(FEEDER_1_MAX_CURRENT, Float.parseFloat(f1_t_c.getText().toString()));
            resultIntent.putExtra(FEEDER_1_MAX_TEMP, Float.parseFloat(f1_t_t.getText().toString()));

            resultIntent.putExtra(FEEDER_2_MAX_VOLTS, Float.parseFloat(f2_t_v.getText().toString()));
            resultIntent.putExtra(FEEDER_2_MAX_CURRENT, Float.parseFloat(f2_t_c.getText().toString()));
            resultIntent.putExtra(FEEDER_2_MAX_TEMP, Float.parseFloat(f2_t_t.getText().toString()));

            setResult(AppCompatActivity.RESULT_OK, resultIntent);

            finish();

        });
    }

}