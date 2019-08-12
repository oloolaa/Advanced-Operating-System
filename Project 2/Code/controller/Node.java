package controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import transportLayer.Channel;

public class Node {
    public int numNodes = 0;
    public int meanD = 0;
    public int meanC = 0;
    public int numRequest = 0;
    public NodeInfo localInfor = null;
    public Map<Integer, NodeInfo> neighbors = new HashMap<>();
    public Map<Integer, Channel> channelRemoteMap = new ConcurrentHashMap<>();

    public void addNeighbor(NodeInfo nodeInfor) {
        neighbors.put(nodeInfor.nodeId, nodeInfor);
    }

    public void setLocalInfo(NodeInfo nodeInfor) {
        localInfor = nodeInfor;
    }

    public Node() {

    }

    public void addChannel(Channel channel) {
        channelRemoteMap.put(channel.channelID, channel);
    }
}
