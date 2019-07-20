import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class CLProtocol {
	public static void startCLProtocol(MapProtocol map) {
		synchronized(map) {
			map.hasReceivedStateMessage[map.id] = true;
			sendMarkerMessage(map, map.id);
		}
	}

	public static void sendMarkerMessage(MapProtocol map, int channel){
		synchronized(map) {
			if (map.colorIsBlue) {
				map.hasReceivedMarker.put(channel, true);
				map.colorIsBlue = false;
				map.currentState.active = map.active;
				map.currentState.vector = map.vector;
				map.currentState.nodeId = map.id;

				int[] clockCopy = new int[map.currentState.vector.length];
				for (int i = 0; i < clockCopy.length; i++) {
					clockCopy[i] = map.currentState.vector[i];
				}
				map.globalSnapshots.add(clockCopy);

				map.saveChannelMsg = 1;

				for (int neighbor : map.neighbors) {
					MarkerMessage message = new MarkerMessage();
					message.nodeId = map.id;
					ObjectOutputStream oos = map.oStream.get(neighbor);
					try {
						oos.writeObject(message);
						oos.flush();
					} catch (IOException e) {

					}
				}

				if ((map.neighbors.size() == 1) && (map.id != 0)) {
					int parent = ConvergeCast.getParent(map.id);
					map.currentState.channelStates = map.channelStates;
					map.colorIsBlue = true;
					map.saveChannelMsg = 0;

					ObjectOutputStream oos = map.oStream.get(parent);
					try {
						oos.writeObject(map.currentState);
						oos.flush();
					} catch (IOException e) {

					}
					map.initialize(map);
				}


			} else {
				map.hasReceivedMarker.put(channel, true);
				int channelCount = 0;
				while (channelCount < map.neighbors.size() && map.hasReceivedMarker.get(map.neighbors.get(channelCount))){
					channelCount++;
				}
				
				if (channelCount == map.neighbors.size() && map.id != 0) {
					map.currentState.channelStates = map.channelStates;
					map.colorIsBlue = true;
					map.saveChannelMsg = 0;
					ObjectOutputStream oos = map.oStream.get(ConvergeCast.getParent(map.id));
					try {
						oos.writeObject(map.currentState);
						oos.flush();
					} catch (IOException e) {

					}
					map.initialize(map);
				}
				
				if (channelCount == map.neighbors.size() && map.id == 0) {
					map.currentState.channelStates = map.channelStates;
					map.stateMessages.put(map.id, map.currentState);
					map.colorIsBlue = true;
					map.saveChannelMsg = 0;
				}
			}
		}
	}

	public static boolean isRunning(MapProtocol map, StateMessage message) {
		int channelCount = 0, stateCount = 0,nodeCount = 0;

		synchronized(map){
			while (nodeCount < map.hasReceivedStateMessage.length && map.hasReceivedStateMessage[nodeCount]) {
				nodeCount++;
			}

			if (nodeCount == map.hasReceivedStateMessage.length) {
				for (stateCount = 0; stateCount < map.stateMessages.size(); stateCount++) {
					if (map.stateMessages.get(stateCount).active) {
						return true;
					}
				}
				
				if (stateCount == map.numOfNodes) {
					for (channelCount=0; channelCount < map.numOfNodes; channelCount++) {
						StateMessage stateMessage = map.stateMessages.get(channelCount);
						for(ArrayList<ApplicationMessage> channel : stateMessage.channelStates.values()){
							if(!channel.isEmpty()){
								return true;
							}
						}
					}
				}

				if(channelCount == map.numOfNodes){
					startOutput(map);
					return false;
				}
			}
		}

		return false;
	}

	public static void saveChannelMessages(int channel, ApplicationMessage applicationMessage, MapProtocol map) {
		synchronized(map){
			if (!map.hasReceivedMarker.get(channel)) {
				if ((map.channelStates.get(channel).isEmpty())) {
					ArrayList<ApplicationMessage> messageList = map.channelStates.get(channel);
					messageList.add(applicationMessage);
					map.channelStates.put(channel, messageList);
				} else if(!(map.channelStates.get(channel).isEmpty())) {
					map.channelStates.get(channel).add(applicationMessage);
				}
			}
		}
	}

	public static void sendToParent(MapProtocol map, StateMessage stateMessage) {
		synchronized(map){
			ObjectOutputStream oos = map.oStream.get(ConvergeCast.getParent(map.id));
			try {
				oos.writeObject(stateMessage);
				oos.flush();
			} catch (IOException e) {

			}
		}
	}

	//Method to send finish message to all the neighbors of the current Node
	public static void startOutput(MapProtocol map) {
		synchronized(map){
			new Output(map).saveSnapshotsToFile();
			for (int neighbor : map.neighbors) {
				OutputMessage message = new OutputMessage();
				ObjectOutputStream oos = map.oStream.get(neighbor);
				try {
					oos.writeObject(message);
					oos.flush();
				} catch (IOException e) {

				}
			}
			System.out.println("Node no." + map.id + " has successfully written an output file");
			System.exit(0);
		}
	}
}

