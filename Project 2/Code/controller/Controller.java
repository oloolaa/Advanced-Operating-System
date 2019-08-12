package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import application.Application;
import transportLayer.TCPChannel;
import transportLayer.TCPClientHandler;
import transportLayer.TCPServerListener;
import utilization.LogicalClock;
import utilization.MessageReceiveService;
import utilization.MessageSenderService;
import utilization.PerformanceMeasurement;
import utilization.VectorClock;

public class Controller {
    public Node myNode;
    public String filename;
    public String transport;
    public String algorithmName;
    public int nodeID;
    public String curDirectory = "";

    public Controller(int nodeID, String configFile, String algorithmName, String transport) {
        this.transport = transport;
        this.filename = configFile;
        this.nodeID = nodeID;
        this.algorithmName = algorithmName;
    }

    public void setDir(String curdir) {
        curDirectory = curdir;
    }

    public void init() {
        Parser.getSingleton().setLocalNodeId(nodeID);
        this.myNode = Parser.getSingleton().parseFile(filename);
        if (transport.toLowerCase().equals("tcp")) {
            initTCPTransport();
        }

        while (!isAllSocketUp(myNode)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            MessageReceiveService.getInstance().connectNode(myNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            MessageSenderService.getInstance().connectNode(myNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        VectorClock.getInstance().init(myNode.numNodes, nodeID);
    }

    public void start() {
        LogicalClock.getInstance().refresh();
        PerformanceMeasurement.getInstance().setDir(curDirectory);
        PerformanceMeasurement.getInstance().init(myNode.numRequest, myNode.localInfor.nodeId);

        Application app = new Application(myNode, algorithmName);
        VectorClock.getInstance().refresh();
        app.setDir(curDirectory);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        app.start();
    }

    // Initiate the TCP server and client
    public void initTCPTransport() {
        this.initTCPServerListener();

        try {
            this.connectTCPChannel(myNode);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Initiate the transport layer service
    private void initTCPServerListener() {
        TCPServerListener server = null;
        try {
            server = new TCPServerListener(myNode.localInfor.port, myNode);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Thread thread = new Thread(server);
        thread.start();
    }

    private void connectTCPChannel(Node myNode) throws InterruptedException {
        for (NodeInfo remoteNode : myNode.neighbors.values()) {
            int try_num = 0;
            if (remoteNode.nodeId > myNode.localInfor.nodeId) {
                continue;
            }
            Socket clientSocket = null;
            while (clientSocket == null) {
                try_num++;
                try {
                    clientSocket = new Socket(remoteNode.hostName, remoteNode.port);
                    System.out.println(remoteNode.hostName + ": " + remoteNode.port + " established successively");

                } catch (UnknownHostException e) {
                    System.out.println("fail to establish tcp connection");
                } catch (IOException e) {
                    System.out.println("fail to establish tcp connection");

                }
                long seconds_to_wait = (long) Math.min(60, Math.pow(2, try_num));
                Thread.sleep(seconds_to_wait * 20);
            }

            TCPChannel tcpChannel = new TCPChannel(remoteNode.nodeId);
            tcpChannel.setSocket(clientSocket);

            myNode.addChannel(tcpChannel);

            PrintWriter outToServer = null;
            try {
                outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            outToServer.println("NODEID:" + myNode.localInfor.nodeId);

            new Thread(new TCPClientHandler(clientSocket, remoteNode.nodeId)).start();

        }

    }

    public boolean isAllSocketUp(Node node) {
        return (node.channelRemoteMap.size() == node.neighbors.size());
    }
}
