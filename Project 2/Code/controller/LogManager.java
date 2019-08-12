package controller;

import java.util.HashMap;
import java.util.Map;

public class LogManager {
    private static LogManager instance = new LogManager();
    String curDirectory = "";
    Map<String, CustomWriter> logMap = new HashMap<>();

    public static LogManager getSingle() {
        return instance;
    }

    public void setDir(String curdir) {
        curDirectory = curdir;
    }

    public CustomWriter getLog(String name) {
    	CustomWriter log;
        if (!logMap.containsKey(curDirectory + name)) {
            log = new CustomWriter();
            logMap.put(curDirectory + name, log);
            log.open(curDirectory + name);
            log.clear();
            return log;
        }
        return logMap.get(curDirectory + name);
    }
}
