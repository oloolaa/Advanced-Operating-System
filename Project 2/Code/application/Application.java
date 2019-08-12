package application;

import java.util.Random;

import controller.LogManager;
import controller.Node;
import lamport.CustomVector;
import lamport.TimeInterval;
import utilization.AlgorithmFactory;
import utilization.MutualExclusiveProtocol;
import utilization.PerformanceMeasurement;
import utilization.VectorClock;

/**
 * Application: The application is responsible for generating critical section requests and then
 * executing critical sections on receiving permission from the mutual exclusion service. Model your
 * application using the following two parameters: inter-request delay, denoted by d, and cs-execution
 * time, denoted by c. The first parameter d denotes the time elapsed between when a node’s current
 * request is satisfied and when it generates the next request. The second parameter c denotes the
 * time a node spends in its critical section. Assume that both d and c are random variables with
 * exponential probability distribution.
 */
public class Application {

    public int interRequestDelay;
    public int csExecutionTimer;
    public Node node;
    public Random rand;
    public MutualExclusiveProtocol protocol;
    public String curDirectory = "";

    public Application(Node node, String algorithmName) {
        rand = new Random();
        interRequestDelay = node.meanD;
        csExecutionTimer = node.meanC;
        this.node = node;
        protocol = AlgorithmFactory.getInstance().getAlgorithm(node, algorithmName);
    }

    public void setDir(String curdir) {
        curDirectory = curdir;
    }

    public void start() {
        LogManager.getSingle().setDir(curDirectory);
        System.out.println("number of requests: " + node.numRequest);
        for (int i = 0; i < node.numRequest; i++) {
            int t1 = nextInterRequestDelay();
            int t2 = nextcsExecutionTimer();
            try {
                Thread.sleep(t1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (i % 100 == 0) {
                System.out.println("application enter cs! at " + (i + 1) + "times with execution time " + t2 + "and interrequest delay " + t1);
            }

            long requestTime = System.currentTimeMillis();

            protocol.csEnter();
            long grantedTime = System.currentTimeMillis();
            CustomVector enterCSTimeStamp = CustomVector.copy(VectorClock.getInstance().toString());

            long enterTime = System.currentTimeMillis();
            try {
                VectorClock.getInstance().tick();
                //System.out.println("nextcsExecutionTimer"+t2);
                Thread.sleep(t2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // How many msgs been sent and received in this cs request, and response time
            long leaveTime = System.currentTimeMillis();
            PerformanceMeasurement.getInstance().updateCSTime(requestTime, leaveTime);
            CustomVector leaveCSTimeStamp = CustomVector.copy(VectorClock.getInstance().toString());

            // TimeInterval data structure:
            // this.enterCSTimeStamp = enterCSTimeStamp;
            // this.leaveCSTimeStamp = leaveCSTimeStamp;
            // this.enterCSSystemTime = enterCSSystemTime;
            // this.leaveCSSystemTime = leaveCSSystemTime;
            // this.nodeId = nodeId;
            TimeInterval curTimeInterval = new TimeInterval(enterCSTimeStamp, enterTime, leaveCSTimeStamp, leaveTime, node.localInfor.nodeId);
            LogManager.getSingle().getLog("TimeInterval-" + node.localInfor.nodeId).log(curTimeInterval.toString());
            VectorClock.getInstance().tick();
            protocol.csLeave();
            System.out.println("Node-" + node.localInfor.nodeId + " leaves C.S!");
        }
    }

    public int nextInterRequestDelay() {
        return (int) (-interRequestDelay * Math.log(1 - rand.nextDouble()));
    }

    public int nextcsExecutionTimer() {
        return (int) (-csExecutionTimer * Math.log(1 - rand.nextDouble()));
    }
}
