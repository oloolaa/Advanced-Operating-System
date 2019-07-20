import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {

}

class ApplicationMessage extends Message implements Serializable{
	String message = "Application";
	int nodeId;
	int[] vector;
}

class MarkerMessage extends Message implements Serializable{
	String message = "Marker";
	int nodeId;
}

class StateMessage extends Message implements Serializable{
	boolean active;
	int nodeId;
	HashMap<Integer,ArrayList<ApplicationMessage>> channelStates;
	int[] vector;
}

class OutputMessage extends Message implements Serializable{
	String message = "Output";
}
