import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class MapProtocol implements Serializable  {
	static String configFileName;

	int numOfNodes;
	int minPerActive;
	int maxPerActive;
	int minSendDelay;
	int snapshotDelay;
	int maxNumber;

	//Variables for MAP Protocol
	int id;
	int[][] graph;
	ArrayList<Integer> neighbors;
	int[] vector;
	boolean active;
	int sentMessageCount;

	//Variables for Chandy Lamport Protocol
	boolean colorIsBlue;
	int saveChannelMsg;
	boolean isFirstSnapshot;

	ArrayList<Node> nodes;
	HashMap<Integer,Socket> channels;						// <Server, Client>
	HashMap<Integer,ArrayList<ApplicationMessage>> channelStates;
	HashMap<Integer,ObjectOutputStream> oStream;			// <Receiver, oos>
	HashMap<Integer,StateMessage> stateMessages;
	HashMap<Integer,Boolean> hasReceivedMarker;
	StateMessage currentState;
	boolean[] hasReceivedStateMessage;
	ArrayList<int[]> globalSnapshots;
	
	public MapProtocol() {
		active=false;
		neighbors = new ArrayList<>();
		sentMessageCount = 0;

		colorIsBlue = true;
		saveChannelMsg=0;
		isFirstSnapshot = true;

		nodes = new ArrayList<>();
		channels = new HashMap<>();
		oStream = new HashMap<>();
		globalSnapshots = new ArrayList<>();
	}
	
	void initialize(MapProtocol map){
		map.channelStates = new HashMap<>();
		map.hasReceivedMarker = new HashMap<>();
		map.stateMessages = new HashMap<>();

		for(Integer receiver : map.channels.keySet()){
			map.channelStates.put(receiver, new ArrayList<>());
		}
		for(Integer neighbor : map.neighbors){
			map.hasReceivedMarker.put(neighbor,false);
		}

		map.hasReceivedStateMessage = new boolean[map.numOfNodes];
		map.currentState = new StateMessage();
		map.currentState.vector = new int[map.numOfNodes];
	}	
}
