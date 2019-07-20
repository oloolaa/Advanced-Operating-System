import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadConfigFile {
	public static MapProtocol readConfigFile(String path) throws FileNotFoundException {
		File file = new File(path);
		MapProtocol map = new MapProtocol();
		map.configFileName = file.getName().split("\\.")[0];

		ArrayList<String> lines = getContent(file);
		String[] numbers = lines.get(0).split(" ");
		map.numOfNodes = Integer.parseInt(numbers[0]);
		map.minPerActive = Integer.parseInt(numbers[1]);
		map.maxPerActive = Integer.parseInt(numbers[2]);
		map.minSendDelay = Integer.parseInt(numbers[3]);
		map.snapshotDelay = Integer.parseInt(numbers[4]);
		map.maxNumber = Integer.parseInt(numbers[5]);
		map.graph = new int[map.numOfNodes][map.numOfNodes];

		for (int i = 1; i <= map.numOfNodes; i++) {
			String[] nodeInfo = lines.get(i).split(" ");
			map.nodes.add(new Node(Integer.parseInt(nodeInfo[0]), nodeInfo[1], Integer.parseInt(nodeInfo[2])));

			String[] neighbors = lines.get(i + map.numOfNodes).split(" ");
			for (String neighbor : neighbors) {
				map.graph[i - 1][Integer.parseInt(neighbor)] = 1;
				map.graph[Integer.parseInt(neighbor)][i - 1] = 1;
			}
		}

		return map;
	}

	private static ArrayList<String> getContent(File file) throws FileNotFoundException {
		ArrayList<String> configurationLines = new ArrayList<>();

		Scanner sc = new Scanner(file);

		while (sc.hasNextLine()) {
			String line = sc.nextLine();

			if (line.length() > 0 && Character.isDigit(line.charAt(0))) {
				int offset = line.indexOf("#");
				if (-1 != offset) {
					line = line.substring(0, offset);
				}

				configurationLines.add(line);
			}
		}

		return configurationLines;
	}
}

