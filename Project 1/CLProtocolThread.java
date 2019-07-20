public class CLProtocolThread extends Thread{
	MapProtocol map;

	public CLProtocolThread(MapProtocol input){
		map = input;
	}

	@Override
	public void run(){
		if (map.isFirstSnapshot) {
			map.isFirstSnapshot = false;
		}
		else{
			try {
				Thread.sleep(map.snapshotDelay);
			} catch (InterruptedException e) {

			}
		}

		CLProtocol.startCLProtocol(map);
	}
}
