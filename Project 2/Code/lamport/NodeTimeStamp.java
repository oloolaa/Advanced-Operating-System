package lamport;

import java.util.Comparator;

public class NodeTimeStamp implements Comparator<NodeTimeStamp>{
	public int timeStamp;
	public int nodeId;
	
	public NodeTimeStamp(int nodeId, int timeStamp){
		this.timeStamp = timeStamp;
		this.nodeId = nodeId;
	}
	
	public int compare(NodeTimeStamp x, NodeTimeStamp y){
		if(x.timeStamp > y.timeStamp) 
			return 1;
		if(x.timeStamp < y.timeStamp) 
			return -1;
		if(x.timeStamp == y.timeStamp){
			if(x.nodeId > y.nodeId) 
				return 1;
			else
				return -1;
		}
		return 0;
	}
}
