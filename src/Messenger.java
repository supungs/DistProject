import java.io.IOException;


public class Messenger {
	Sender sender;
	Receiver receiver;
	
	public Messenger(){
		sender=new Sender();
		try {
			receiver=new Receiver(Config.my_port, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Send a Message object
	 */
	public Message sendMessage(Message msg){
		char[] temp=sender.sendTo(msg.toString(), msg.ip_to, msg.port_to);
		String resp=cleanReceived(temp);
		return parseMsg(resp);
	}
	
	/*
	 * Get called when a message is recieved
	 */
	public void onReceive(char[] buf){
		String temp=cleanReceived(buf);
	}
	
	/*
	 * Clean the received buffer using the LENTGTH parameter
	 */
	private String cleanReceived(char[] buf){
		String temp=new String(buf);
		String lenStr=temp.substring(0, 4);
		int len=Integer.parseInt(lenStr);
		if(len>0) return temp.substring(5,len);
		return null;		
	}
	
	/*
	 * Parse the message into a Message object
	 */
	private Message parseMsg(String msg){
		int len=msg.length();
		if(msg.substring(0, 5).equals("REGOK"))
			return new RegResult(msg.substring(6,len));
		else if(msg.substring(0, 5).equals("UNROK"))
			return new UnRegResult(msg.substring(6,len));
		return null;
	}
}
