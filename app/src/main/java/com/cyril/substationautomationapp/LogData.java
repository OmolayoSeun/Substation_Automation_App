package com.cyril.substationautomationapp;

public class LogData {
    public float feeder1Volt, feeder2Volt,
            feeder1Current, feeder2Current,
            feeder1Temp, feeder2Temp,
            feeder1PowerFactor, feeder2PowerFactor;
    public long timestamp;

    public LogData() {
        // Default constructor required for calls to DataSnapshot.getValue(LogData.class)
    }

    public String toString() {
        return " \"" + timestamp + "\": {\n" +
                "\"feeder1Volt\": " + feeder1Volt + ",\n" +
                "\"feeder2Volt\": " + feeder2Volt + ",\n" +
                "\"feeder1Current\": " + feeder1Current + ",\n" +
                "\"feeder2Current\": " + feeder2Current + ",\n" +
                "\"feeder1Temp\": " + feeder1Temp + ",\n" +
                "\"feeder2Temp\": " + feeder2Temp + ",\n" +
                "\"feeder1PowerFactor\": " + feeder1PowerFactor + ",\n" +
                "\"feeder2PowerFactor\": " + feeder2PowerFactor + ",\n" +
                "    },\n";
    }

}
