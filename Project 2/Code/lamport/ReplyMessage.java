package lamport;


public class ReplyMessage extends Message {	
	public ReplyMessage(){
		setType(MessageFactory.getSingleton().typeReply);
	}	
}
