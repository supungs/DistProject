import java.io.IOException;


public class Messenger {
	Sender sender;
	Receiver receiver;
	FileSharer sharer;
	
	public Messenger(FileSharer sharer){
		sender=new Sender();
		this.sharer=sharer;
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
		if(msg.type==MsgType.MSG_REG || msg.type==MsgType.MSG_UNREG){
			char[] temp=sender.sendTCP(msg.toString(), msg.ip_to, msg.port_to);
			String resp=cleanReceived(temp);
			return parseMsg(resp);
		}else{
			sender.sendUDP(msg.toString(), msg.ip_to, msg.port_to);
			return new Message();
		}
	}
	
	/*
	 * Get called when a message is recieved
	 *
	 */
	public void onReceive(String msg){
		String temp=cleanReceived(msg.toCharArray());
		System.out.println("MSG RECEIVED: "+ temp);
		Message message=parseMsg(temp);
		sharer.onPeerRequest(message);
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
		else if(msg.substring(0, 6).equals("JOINOK"))
			return new JoinResult(msg.substring(7,len));
		else if(msg.substring(0, 4).equals("JOIN"))
			return new JoinMessage(msg.substring(5,len));
		else if(msg.substring(0, 7).equals("LEAVEOK"))
			return new LeaveResult(msg.substring(8,len));
		else if(msg.substring(0, 5).equals("LEAVE"))
			return new LeaveMessage(msg.substring(6,len));
		else if(msg.substring(0, 5).equals("SEROK"))
			return new QueryResponseMessage(msg.substring(6,len));
		else if(msg.substring(0, 3).equals("SER"))
			return new QueryMessage(msg.substring(4,len));
		return null;
	}
}
