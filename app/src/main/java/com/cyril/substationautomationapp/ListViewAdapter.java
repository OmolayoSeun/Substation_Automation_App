package com.cyril.substationautomationapp;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends ArrayAdapter<LogData> {
    public ListViewAdapter(@NonNull Context context, @NonNull List<LogData> values) {
        super(context, R.layout.custom_list_layout, values);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LogData logData = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_layout, parent, false);
        }

        TextView timestamp = convertView.findViewById(R.id.timestamp_id);
        TextView feeder1Volt = convertView.findViewById(R.id.feeder1_volt_id);
        TextView feeder2Volt = convertView.findViewById(R.id.feeder2_volt_id);
        TextView feeder1Current = convertView.findViewById(R.id.feeder1_current_id);
        TextView feeder2Current = convertView.findViewById(R.id.feeder2_current_id);
        TextView feeder1Temp = convertView.findViewById(R.id.feeder1_temp_id);
        TextView feeder2Temp = convertView.findViewById(R.id.feeder2_temp_id);
        TextView feeder1PowerFactor = convertView.findViewById(R.id.feeder1_power_factor_id);
        TextView feeder2PowerFactor = convertView.findViewById(R.id.feeder2_power_factor_id);


        assert logData != null;

        LocalDateTime dateTime;
        String formatted = String.valueOf(logData.timestamp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateTime = Instant.ofEpochSecond(logData.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            formatted = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        timestamp.setText(/*"Timestamp: " + */ formatted);
        feeder1Volt.setText(String.format(Locale.US, "%.2f",logData.feeder1Volt));
        feeder2Volt.setText(String.format(Locale.US, "%.2f",logData.feeder2Volt));
        feeder1Current.setText(String.format(Locale.US, "%.2f",logData.feeder1Current));
        feeder2Current.setText(String.format(Locale.US, "%.2f",logData.feeder2Current));
        feeder1Temp.setText(String.format(Locale.US, "%.2f",logData.feeder1Temp));
        feeder2Temp.setText(String.format(Locale.US, "%.2f",logData.feeder2Temp));
        feeder1PowerFactor.setText(String.format(Locale.US, "%.2f",logData.feeder1PowerFactor));
        feeder2PowerFactor.setText(String.format(Locale.US, "%.2f",logData.feeder2PowerFactor));

        return convertView;
    }
}
