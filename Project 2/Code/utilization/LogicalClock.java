package utilization;

public class LogicalClock {
    private static LogicalClock instance = new LogicalClock();
    int clock;

    public static LogicalClock getInstance() {
        return instance;
    }

    public LogicalClock() {
        clock = 1;
    }

    public void refresh() {
        clock = 1;
    }

    public int getValue() {
        return clock;
    }

    public void tick() {
        clock++;
    }

    public void sendAction() {
        clock++;
    }

    public void receiveAction(int sentValue) {
        clock = Math.max(clock, sentValue) + 1;
    }
}
