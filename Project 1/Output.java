import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Output {
	MapProtocol map;

	public Output(MapProtocol input) {
		map = input;
	}


	public void saveSnapshotsToFile() {
		String fileName = MapProtocol.configFileName + "-" + map.id + ".out";

		synchronized(map.globalSnapshots){
			try {
				File file = new File(fileName);
				FileWriter fW;
				if (file.exists()) {
					fW = new FileWriter(file,true);
				} else {
					fW = new FileWriter(file);
				}
				BufferedWriter bW = new BufferedWriter(fW);

   
				for (int i = 0; i < map.globalSnapshots.size(); i++) {
					for(int j : map.globalSnapshots.get(i)){
						bW.write(j + " ");
						
					}

					if (i < (map.globalSnapshots.size() - 1)) {
						bW.write("\n");
					}
				}

				map.globalSnapshots.clear();
				bW.close();
			}
			catch(IOException ex) {

			}
		}
	}

}

