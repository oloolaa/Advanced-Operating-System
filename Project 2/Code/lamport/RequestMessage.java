package lamport;


public class RequestMessage extends Message {		
	public RequestMessage(){
		setType(MessageFactory.getSingleton().typeRequest);
	}
}
