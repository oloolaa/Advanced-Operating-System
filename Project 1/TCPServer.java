import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer{
	ServerSocket server;
	Socket socket;
	int serverPort;
	private MapProtocol map;
	
	public TCPServer(MapProtocol input) {
		map = input;
		serverPort = input.nodes.get(input.id).listenPort;
		try {
			server = new ServerSocket(serverPort);
		} catch (IOException e) {

		}

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {

		}
	}
	
	public void listenForInput(){
		try {
			while (true) {
				try {
					socket = server.accept();
				} catch (IOException e) {

				}
				new ReceiveMessageThread(socket, map).start();
			}
		}
		finally {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class ReceiveMessageThread extends Thread {
	Socket socket;
	MapProtocol map;

	public ReceiveMessageThread(Socket socket, MapProtocol map) {
		this.socket = socket;
		this.map = map;
	}

	public void run() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (true) {
			try {
				Message message = (Message)ois.readObject();

				synchronized(map) {
					boolean running;

					if (message instanceof MarkerMessage) {
						CLProtocol.sendMarkerMessage(map, ((MarkerMessage) message).nodeId);
					} else if (message instanceof ApplicationMessage && !map.active && map.sentMessageCount < map.maxNumber && map.saveChannelMsg == 0) {
						map.active = true;
						new SendMessageThread(map).start();
					} else if (message instanceof ApplicationMessage && !map.active && map.saveChannelMsg == 1) {
						CLProtocol.saveChannelMessages(((ApplicationMessage) message).nodeId, (ApplicationMessage)message, map);
					} else if (message instanceof StateMessage) {
						if (map.id == 0) {
							map.stateMessages.put(((StateMessage) message).nodeId, ((StateMessage)message));
							map.hasReceivedStateMessage[((StateMessage) message).nodeId] = true;

							if (map.stateMessages.size() == map.numOfNodes){
								running = CLProtocol.isRunning(map,((StateMessage)message));
								if (running) {
									map.initialize(map);
									new CLProtocolThread(map).start();
								}
							}
						} else {
							CLProtocol.sendToParent(map,((StateMessage)message));
						}
					} else if (message instanceof OutputMessage) {
						CLProtocol.startOutput(map);
					}

					if (message instanceof ApplicationMessage) {
						for (int i = 0; i < map.numOfNodes; i++) {
							map.vector[i] = Math.max(map.vector[i], ((ApplicationMessage) message).vector[i]);
						}
						map.vector[map.id]++;
					}
				}
			} catch (ClassNotFoundException | IOException e) {

			}
		}
	}
}