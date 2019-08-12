package lamport;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import utilization.LogicalClock;
import utilization.MessageReceiveService;
import utilization.MessageSenderService;
import utilization.MutualExclusiveProtocol;
import utilization.ReceiveMessageInterface;

public class LamportAlgorithm implements MutualExclusiveProtocol, ReceiveMessageInterface {

    // for optimizing lamport alg, rcvlist records the incoming message with
    // higher timestamp than local request
    Set<Integer> rcvlist = new HashSet<Integer>();

    int localId;
    int numOfNode;
    ReceiveMessageInterface application = null;

    PriorityBlockingQueue<NodeTimeStamp> pqueue;
    NodeTimeStamp localRequestStamp;


    public LamportAlgorithm(int numOfNode, int localId) {

        Comparator<NodeTimeStamp> comparator = new NodeTimeStamp(0, 0);
        pqueue = new PriorityBlockingQueue<NodeTimeStamp>(numOfNode, comparator);
        localRequestStamp = new NodeTimeStamp(localId, Integer.MAX_VALUE);

        this.localId = localId;
        this.numOfNode = numOfNode;
        MessageReceiveService.getInstance().register(this);

    }

    public void receive(String message, int channel) {
        Message msg = MessageParser.getSingleton().parser(message);

        String type = msg.getType();
        if (type == null) {
            return;
        }
        int rtimestamp = msg.getTimpeStamp();
        NodeTimeStamp receivedTimeStamp = new NodeTimeStamp(channel, rtimestamp);

        if (!rcvlist.contains(receivedTimeStamp.nodeId)
                && localRequestStamp.compare(receivedTimeStamp, localRequestStamp) == 1) {
            rcvlist.add(receivedTimeStamp.nodeId);
        }

        if (type.equals(MessageFactory.getSingleton().typeRequest)) {
            handlerRequest((RequestMessage) msg, channel);
        } else if (type.equals(MessageFactory.getSingleton().typeRelease)) {
            handlerRelease((ReleaseMessage) msg, channel);
        } else if (type.equals(MessageFactory.getSingleton().typeReply)) {
            handlerReply((ReplyMessage) msg, channel);
        }

        // if priorityqueue is not empty 
        // and head of pq equas local id
        // and rcvlist size = neighbor size
        // then it can inform application to enter critical section
        if ((!pqueue.isEmpty()) && pqueue.peek().nodeId == localId && rcvlist.size() == numOfNode - 1) {
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    public void handlerRequest(RequestMessage msg, int channel) {
        int rtimestamp = msg.getTimpeStamp();
        NodeTimeStamp receivedTimeStamp = new NodeTimeStamp(channel, rtimestamp);
        // add incoming msg into pq
        pqueue.add(receivedTimeStamp);

        // send reply message to incoming process
        String type = MessageFactory.getSingleton().typeReply;
        Message reply = preSentMessage(type);
        MessageSenderService.getInstance().send(reply.toString(), channel);
    }

    // construct outgoing message
    private Message preSentMessage(String type) {
        Message msg = MessageFactory.getSingleton().createMessage(type);
        int stimeStamp = LogicalClock.getInstance().getValue();
        msg.setTimpeStamp(stimeStamp);
        msg.setNodeId(localId);
        return msg;
    }

    public void handlerRelease(ReleaseMessage message, int channel) {
        // remove the head of priorityqueue when receving release message
        if (!pqueue.isEmpty()) {
            pqueue.poll();
        }
    }

    public void handlerReply(ReplyMessage message, int channel) {
        // we do not need to process reply message
        // because in receive processing, we have update hashset "rcvlist"
    }

    @Override
    public synchronized void csEnter() {
        String type = MessageFactory.getSingleton().typeRequest;
        RequestMessage msg = (RequestMessage) MessageFactory.getSingleton().createMessage(type);

        localRequestStamp.timeStamp = LogicalClock.getInstance().getValue();
        msg.setTimpeStamp(localRequestStamp.timeStamp);
        msg.setNodeId(localId);

        // local request, update local priority queue
        pqueue.add(localRequestStamp);

        // broadcast request message to all neighbors
        MessageSenderService.getInstance().sendBroadCast(msg.toString());

        // if head of pr is local id
        // and rcvlist size = neighbor size, it means node has received higher timestamp message from all neihbors
        // then it can enter into critical section
        // otherwise, it needs to wait
        if (!(pqueue.peek().nodeId == localId && rcvlist.size() == numOfNode - 1)) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void csLeave() {

        localRequestStamp.timeStamp = Integer.MAX_VALUE;

        // clear rcvlist 
        rcvlist.clear();

        // when leaving cs, remove head of priority queue
        if (!pqueue.isEmpty()) {
            pqueue.poll();
        }

        // send release message to all neighbors
        String type = MessageFactory.getSingleton().typeRelease;
        Message release = preSentMessage(type);
        MessageSenderService.getInstance().sendBroadCast(release.toString());
    }

}
