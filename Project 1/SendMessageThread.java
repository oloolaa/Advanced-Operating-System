import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

public class SendMessageThread extends Thread{
	MapProtocol map;

	public SendMessageThread(MapProtocol map) {
		this.map = map;
	}

	public void sendMessages() {
		int messageCount, minSendDelay;
		synchronized(map) {
			messageCount = getRandomNumber(map.minPerActive, map.maxPerActive);
			if (messageCount == 0) {
				messageCount = getRandomNumber(map.minPerActive + 1, map.maxPerActive);
			}
			minSendDelay = map.minSendDelay;
		}

		for (int i = 0; i < messageCount; i++) {
			synchronized(map){
				int currentNeighbor = map.neighbors.get(getRandomNumber(0, map.neighbors.size() - 1));

				if (map.active == true) {
					ApplicationMessage message = new ApplicationMessage();
					map.vector[map.id]++;

					message.vector = new int[map.vector.length];
					for (int j = 0; j < map.vector.length; j++) {
						message.vector[j] = map.vector[j];
					}
					message.nodeId = map.id;

					try {
						ObjectOutputStream oos = map.oStream.get(currentNeighbor);
						oos.writeObject(message);
						oos.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}

					map.sentMessageCount++;
				}
			}

			try{
				Thread.sleep(minSendDelay);
			}
			catch (InterruptedException e) {

			}
		}

		synchronized(map){
			map.active = false;
		}
	}

	@Override
	public void run(){
		sendMessages();
	}

	int getRandomNumber(int min,int max){
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}
}
