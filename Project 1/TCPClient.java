import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	public TCPClient(MapProtocol map, int currentNode) {
		for (int i = 0; i < map.numOfNodes; i++) {
			if(map.graph[currentNode][i] == 1){
				String hostName = map.nodes.get(i).hostName;
				int port = map.nodes.get(i).listenPort;

				InetAddress address = null;
				try {
					address = InetAddress.getByName(hostName);
				} catch (UnknownHostException e) {

				}

				Socket client = null;
				try {
					client = new Socket(address,port);
				} catch (IOException e) {

				}

				map.channels.put(i, client);
				map.neighbors.add(i);

				ObjectOutputStream oos = null;
				try {
					oos = new ObjectOutputStream(client.getOutputStream());
				} catch (IOException e) {

				}
				map.oStream.put(i, oos);
			}
		}
	}
}
