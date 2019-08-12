package utilization;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import controller.Node;

public class MessageReceiveService implements ReceiveMessageInterface {
    Node node = null;
    private static MessageReceiveService instance = new MessageReceiveService();
    protected List<ReceiveMessageInterface> listenerList = new CopyOnWriteArrayList<>();

    public static MessageReceiveService getInstance() {
        return instance;
    }

    public void connectNode(Node node) throws IOException {
        this.node = node;
    }

    public synchronized void receive(String msg, int channelID) {
        PerformanceMeasurement.getInstance().addReceiveMessageCount();

        if (msg.startsWith("SCALARTIME:")) {
            //SCALARTIME
            String tmp = msg.substring(11, msg.indexOf(';'));
            LogicalClock.getInstance().receiveAction(Integer.parseInt(tmp));

            // VECTOR CLOCK
            int vcStartPos = msg.indexOf("VECTORCLOCK:", 10);
            int vcEndPos = msg.indexOf(";", vcStartPos + 12);
            String vc = msg.substring(vcStartPos + 12, vcEndPos + 1);
            VectorClock.getInstance().receiveAction(vc);

            for (ReceiveMessageInterface obj : listenerList) {
                obj.receive(msg.substring(vcEndPos + 1), channelID);
            }
        }
    }

    public void register(ReceiveMessageInterface obj) {
        listenerList.add(obj);
    }

    public void unregister(ReceiveMessageInterface obj) {
        listenerList.remove(obj);
    }
}
