package utilization;

import java.io.IOException;
import java.io.RandomAccessFile;

public class PerformanceMeasurement {
    private long[] sendMessageCount = null;
    private long[] receiveMessageCount = null;
    private long[] enterCSTimes = null;
    private long[] responseTime = null;
    private long receiveTotal = 0;
    private long sendTotal = 0;
    public int count = 0;
    public int interval;
    public RandomAccessFile pmFile;
    public int nodeid;
    public String curDirectory = "";
    private static PerformanceMeasurement instance = new PerformanceMeasurement();

    public static PerformanceMeasurement getInstance() {
        return instance;
    }

    public void setDir(String curdir) {
        curDirectory = curdir;
    }

    public void init(int interval, int nodeid) {
        this.sendMessageCount = new long[interval];
        this.receiveMessageCount = new long[interval];
        this.enterCSTimes = new long[interval];
        this.responseTime = new long[interval];
        this.interval = interval;
        this.nodeid = nodeid;
        receiveTotal = 0;
        sendTotal = 0;
        count = 0;
        String fileName = curDirectory + "performance-" + nodeid;
        try {
            pmFile = new RandomAccessFile(fileName, "rw");
            pmFile.setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void updateCSTime(long requestT, long grantT) {
        this.sendMessageCount[count] = this.sendTotal;
        this.receiveMessageCount[count] = this.receiveTotal;
        this.enterCSTimes[count] = count + 1;
        this.responseTime[count] = grantT - requestT;
        this.toFile();
        count++;
    }

    public synchronized void addSendMessageCount() {
        sendTotal++;
    }

    public synchronized void addReceiveMessageCount() {
        receiveTotal++;
    }

    public void toFile() {
        StringBuilder res = new StringBuilder();
        int i = count;
        res.append(this.enterCSTimes[i]);
        res.append(',');
        res.append(this.receiveMessageCount[i]);
        res.append(',');
        res.append(this.sendMessageCount[i]);
        res.append(',');
        res.append(this.responseTime[i]);
        res.append(';');
        res.append(System.getProperty("line.separator"));

        try {
            pmFile.writeBytes(res.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        res.setLength(0);
    }
}
