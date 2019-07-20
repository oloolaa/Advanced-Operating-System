import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		int initiator = 0;
		MapProtocol map = ReadConfigFile.readConfigFile("/home/012/y/yx/yxc180006/Project1/config.txt");
		map.id = Integer.parseInt(args[0]);
		int currentNode = map.id;

		ConvergeCast.constructNodeTree(map.graph);
	
		TCPServer server = new TCPServer(map);
		new TCPClient(map, currentNode);

		map.vector = new int[map.numOfNodes];
		map.initialize(map);

		if (currentNode == initiator) {
			map.active = true;
			new CLProtocolThread(map).start();
			new SendMessageThread(map).start();
		}
		
		server.listenForInput();
	}
}
