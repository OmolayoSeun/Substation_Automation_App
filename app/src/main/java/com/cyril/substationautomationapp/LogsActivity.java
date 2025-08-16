package com.cyril.substationautomationapp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Collections;
import java.util.List;

public class LogsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setTitle("Logs");


        ListView listView = findViewById(R.id.log_list_view);
        List<LogData> logDataList = StaticData.getInstance().getLogDataList();

        if (logDataList != null) {
            Collections.reverse(logDataList);
            ListViewAdapter adapter = new ListViewAdapter(this, logDataList);
            listView.setAdapter(adapter);
        }
        else {
            Toast.makeText(this, "No logs found", Toast.LENGTH_LONG).show();
        }
    }
}