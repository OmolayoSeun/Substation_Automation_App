package com.cyril.substationautomationapp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Intent logsIntent, settingIntent;
    FirebaseDatabase database;
    TextView feeder1Volt, feeder2Volt,
            feeder1Current, feeder2Current, feeder1Temp, feeder2Temp,
            feeder1PowerFactor, feeder2PowerFactor;
    ProgressBar feeder1VoltProgress, feeder1CurrentProgress, feeder1TempProgress, feeder2VoltProgress,
            feeder2CurrentProgress, feeder2TempProgress;

    ActivityResultLauncher<Intent> settingsLauncher;
    float feeder1MaxVolts, feeder1MaxCurrent, feeder1MaxTemp, feeder2MaxVolts, feeder2MaxCurrent, feeder2MaxTemp;


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch feeder1Switch, feeder2Switch;
    DatabaseReference logRef, feeder1StatusRef,
            feeder1VoltThresholdRef, feeder1CurrentThresholdRef, feeder1TempThresholdRef, feeder2StatusRef,
            feeder2VoltThresholdRef, feeder2CurrentThresholdRef, feeder2TempThresholdRef;
    List<LogData> logDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setTitle("Substation Automation App");
        logsIntent = new Intent(this, LogsActivity.class);
        settingIntent = new Intent(this, SettingsActivity.class);
        database = FirebaseDatabase.getInstance();

        logRef = database.getReference("logs");
        feeder1StatusRef = database.getReference("feeder1Status");
        feeder1VoltThresholdRef = database.getReference("feeder1VoltThreshold");
        feeder1CurrentThresholdRef = database.getReference("feeder1CurrentThreshold");
        feeder1TempThresholdRef = database.getReference("feeder1TempThreshold");
        feeder2StatusRef = database.getReference("feeder2Status");
        feeder2VoltThresholdRef = database.getReference("feeder2VoltThreshold");
        feeder2CurrentThresholdRef = database.getReference("feeder2CurrentThreshold");
        feeder2TempThresholdRef = database.getReference("feeder2TempThreshold");

        feeder1Switch = findViewById(R.id.feeder1_switch);
        feeder2Switch = findViewById(R.id.feeder2_switch);
        feeder1Volt = findViewById(R.id.feeder1_volt);
        feeder2Volt = findViewById(R.id.feeder2_volt);
        feeder1Current = findViewById(R.id.feeder1_current);
        feeder2Current = findViewById(R.id.feeder2_current);
        feeder1Temp = findViewById(R.id.feeder1_temperature);
        feeder2Temp = findViewById(R.id.feeder2_temperature);
        feeder1PowerFactor = findViewById(R.id.feeder1_power_factor);
        feeder2PowerFactor = findViewById(R.id.feeder2_power_factor);

        feeder1VoltProgress = findViewById(R.id.feeder1_volt_progress);
        feeder1CurrentProgress = findViewById(R.id.feeder1_current_progress);
        feeder1TempProgress = findViewById(R.id.feeder1_temperature_progress);

        feeder2VoltProgress = findViewById(R.id.feeder2_volt_progress);
        feeder2CurrentProgress = findViewById(R.id.feeder2_current_progress);
        feeder2TempProgress = findViewById(R.id.feeder2_temperature_progress);


        feeder1VoltThresholdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feeder1MaxVolts = snapshot.getValue(float.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        feeder1CurrentThresholdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feeder1MaxCurrent = snapshot.getValue(float.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        feeder1TempThresholdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feeder1MaxTemp = snapshot.getValue(float.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        feeder2VoltThresholdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feeder2MaxVolts = snapshot.getValue(float.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        feeder2CurrentThresholdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feeder2MaxCurrent = snapshot.getValue(float.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        feeder2TempThresholdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                feeder2MaxTemp = snapshot.getValue(float.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        startFirebaseListenerService();

        logRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                logDataList = new ArrayList<>();

                // Iterate through all the children (timestamps)
                for (DataSnapshot timestampSnapshot : snapshot.getChildren()) {
                    // Get the data for each timestamp

                    LogData logData = timestampSnapshot.getValue(LogData.class);
                    assert logData != null;
                    logData.timestamp = Long.parseLong(Objects.requireNonNull(timestampSnapshot.getKey()));
                    logDataList.add(logData);
                }

                // Update the TextViews with the latest data
                if (!logDataList.isEmpty()) {
                    LogData latestData = logDataList.get(logDataList.size() - 1);
                    feeder1Volt.setText(String.format(Locale.US, "%.2f", latestData.feeder1Volt));
                    feeder2Volt.setText(String.format(Locale.US, "%.2f", latestData.feeder2Volt));
                    feeder1Current.setText(String.format(Locale.US, "%.2f", latestData.feeder1Current));
                    feeder2Current.setText(String.format(Locale.US, "%.2f", latestData.feeder2Current));
                    feeder1Temp.setText(String.format(Locale.US, "%.2f", latestData.feeder1Temp));
                    feeder2Temp.setText(String.format(Locale.US, "%.2f", latestData.feeder2Temp));
                    feeder1PowerFactor.setText(String.format(Locale.US, "%.2f", latestData.feeder1PowerFactor));
                    feeder2PowerFactor.setText(String.format(Locale.US, "%.2f", latestData.feeder2PowerFactor));

                    feeder1VoltProgress.setProgress((int) (latestData.feeder1Volt / feeder1MaxVolts * 100));
                    feeder1CurrentProgress.setProgress((int) (latestData.feeder1Current / feeder1MaxCurrent * 100));
                    feeder1TempProgress.setProgress((int) (latestData.feeder1Temp / feeder1MaxTemp * 100));
                    feeder2VoltProgress.setProgress((int) (latestData.feeder2Volt / feeder2MaxVolts * 100));
                    feeder2CurrentProgress.setProgress((int) (latestData.feeder2Current / feeder2MaxCurrent * 100));
                    feeder2TempProgress.setProgress((int) (latestData.feeder2Temp / feeder2MaxTemp * 100));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        feeder1StatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean status = Boolean.TRUE.equals(snapshot.getValue(boolean.class));

                feeder1Switch.setChecked(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        feeder2StatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean status = Boolean.TRUE.equals(snapshot.getValue(boolean.class));

                feeder2Switch.setChecked(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        feeder1Switch.setOnClickListener(v -> feeder1StatusRef.setValue(feeder1Switch.isChecked()));
        feeder2Switch.setOnClickListener(v -> feeder2StatusRef.setValue(feeder2Switch.isChecked()));

        ////////////////Start/////////////////

        settingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // Extract the updated values
                            feeder1MaxVolts = data.getFloatExtra(SettingsActivity.FEEDER_1_MAX_VOLTS, feeder1MaxVolts);
                            feeder1MaxCurrent = data.getFloatExtra(SettingsActivity.FEEDER_1_MAX_CURRENT,feeder1MaxCurrent);
                            feeder1MaxTemp = data.getFloatExtra(SettingsActivity.FEEDER_1_MAX_TEMP, feeder1MaxTemp);
                            feeder2MaxVolts = data.getFloatExtra(SettingsActivity.FEEDER_2_MAX_VOLTS, feeder2MaxVolts);
                            feeder2MaxCurrent = data.getFloatExtra(SettingsActivity.FEEDER_2_MAX_CURRENT, feeder2MaxCurrent);
                            feeder2MaxTemp = data.getFloatExtra(SettingsActivity.FEEDER_2_MAX_TEMP, feeder2MaxTemp);


                            feeder1VoltThresholdRef.setValue(feeder1MaxVolts);
                            feeder1CurrentThresholdRef.setValue(feeder1MaxCurrent);
                            feeder1TempThresholdRef.setValue(feeder1MaxTemp);
                            feeder2VoltThresholdRef.setValue(feeder2MaxVolts);
                            feeder2CurrentThresholdRef.setValue(feeder2MaxCurrent);
                            feeder2TempThresholdRef.setValue(feeder2MaxTemp);


                            Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show();
                        }
                    } else if (result.getResultCode() == AppCompatActivity.RESULT_CANCELED) {
                        //Toast.makeText(this, "Failed to Update database", Toast.LENGTH_SHORT).show();
                        // Handle cancellation if needed
                    }
                });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.view_logsID) {

            StaticData.getInstance().setLogDataList(logDataList);

            startActivity(logsIntent);
            return true;
        } else if (item.getItemId() == R.id.settingsID) {

             settingIntent.putExtra(SettingsActivity.FEEDER_1_MAX_VOLTS, feeder1MaxVolts);
             settingIntent.putExtra(SettingsActivity.FEEDER_1_MAX_CURRENT, feeder1MaxCurrent);
             settingIntent.putExtra(SettingsActivity.FEEDER_1_MAX_TEMP, feeder1MaxTemp);
             settingIntent.putExtra(SettingsActivity.FEEDER_2_MAX_VOLTS, feeder2MaxVolts);
             settingIntent.putExtra(SettingsActivity.FEEDER_2_MAX_CURRENT, feeder2MaxCurrent);
             settingIntent.putExtra(SettingsActivity.FEEDER_2_MAX_TEMP, feeder2MaxTemp);

            settingsLauncher.launch(settingIntent);
            return true;
        } else return super.onOptionsItemSelected(item);

    }


    private void startFirebaseListenerService() {

        Intent serviceIntent = new Intent(this, FirebaseBackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
}