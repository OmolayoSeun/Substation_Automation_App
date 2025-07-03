package com.cyril.substationautomationapp;

import java.util.List;

public class StaticData {

    private List<LogData> logDataList;
    private static StaticData instance;
    private StaticData() {}
    public static StaticData getInstance() {
        if (instance == null) {
            instance = new StaticData();
        }
        return instance;
    }

    public void setLogDataList(List<LogData> list) {
        this.logDataList = list;
    }

    public List<LogData> getLogDataList() {
        return logDataList;
    }

}
