package lamport;


public class ReleaseMessage extends Message {	
	public ReleaseMessage(){
		setType(MessageFactory.getSingleton().typeRelease);
	}	
}
