package utilization;

import java.io.IOException;

import controller.Node;
import transportLayer.Channel;

public class MessageSenderService implements SendMessageInterface {
    Node node = null;
    private static MessageSenderService instance = new MessageSenderService();

    private MessageSenderService() {

    }

    public static MessageSenderService getInstance() {
        return instance;
    }

    public void connectNode(Node node) throws IOException {
        this.node = node;
    }

    @Override
    public synchronized void send(String message, int channelID) {
        Channel channel = node.channelRemoteMap.get(channelID);

        PerformanceMeasurement.getInstance().addSendMessageCount();
        LogicalClock.getInstance().sendAction();
        VectorClock.getInstance().sendAction();

        channel.send("SCALARTIME:" + LogicalClock.getInstance().getValue() + ";" + "VECTORCLOCK:" + VectorClock.getInstance().toString() + ";" + message);
    }

    public void sendBroadCast(String message) {
        for (Integer i : node.channelRemoteMap.keySet()) {
            send(message, i);
        }
    }
}
