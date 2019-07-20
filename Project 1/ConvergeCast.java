import java.util.LinkedList;
import java.util.Queue;

public class ConvergeCast {
	static int[] parent;

	public static int getParent(int id) {
		return parent[id];
	}

	public static void constructNodeTree(int[][] graph){
		boolean[] visited = new boolean[graph.length];
		parent = new int[graph.length];

		Queue<Integer> queue = new LinkedList<>();
		queue.add(0);

		parent[0] = 0;
		visited[0] = true;

		while(!queue.isEmpty()){
			int node = queue.remove();
			for (int i = 0; i < graph[node].length; i++) {
				if (graph[node][i] == 1 && visited[i] == false) {
					queue.add(i);
					ConvergeCast.parent[i] = node;
					visited[i] = true;
				}
			}
		}
	}
}
