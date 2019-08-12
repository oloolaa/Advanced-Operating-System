package utilization;

public class VectorClock {
    private static VectorClock instance = new VectorClock();
    public int numProcess;
    public int localNodeId;
    int clock[] = null;

    private VectorClock() {

    }

    public static VectorClock getInstance() {
        return instance;
    }

    public void init(int numPro, int nodeId) {
        numProcess = numPro;
        localNodeId = nodeId;
        clock = new int[numPro];
        for (int i = 0; i < numProcess; i++) {
            clock[i] = 0;
        }
        clock[localNodeId]++;
    }

    public void refresh() {
        for (int i = 0; i < numProcess; i++) {
            clock[i] = 0;
        }
        clock[localNodeId]++;
    }

    public void sendAction() {
        clock[localNodeId]++;
    }

    public void tick() {
        clock[localNodeId]++;
    }

    public void receiveAction(String vc) {
        int num = 0, count = 0;

        for (int i = 0; i < vc.length(); i++) {
            char ch = vc.charAt(i);
            if (Character.isDigit(ch)) {
                num = num * 10 + ch - '0';
            } else {
                clock[count] = Math.max(clock[count], num);
                count++;
                num = 0;
            }
        }
        clock[localNodeId]++;
    }

    public String toString() {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < numProcess; i++) {
            stb.append(clock[i]);
            stb.append(',');
        }
        return stb.substring(0, stb.length() - 1);
    }
}
