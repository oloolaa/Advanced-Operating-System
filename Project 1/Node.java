public class Node {
	int nodeId;
	String hostName;
	int listenPort;

	public Node(int id, String host, int port) {
		super();
		nodeId = id;
		hostName = host;
		listenPort = port;
	}
}