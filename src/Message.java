
public class Message {
	public MsgType type;
	public String ip_from, ip_to;
	public int port_from, port_to;
	public String username;
	
	public Message(){
		ip_from=Config.my_ip;
		port_from=Config.my_port;
	}
	
	public String toString() {
		return "";
	}
	public MsgType getType(){
		return type;
	}
	
	protected static String getLength(String msg){
		int len=msg.length()+4;
		return String.format("%04d", len);
	}
}

enum MsgType { MSG_REG, MSG_REGOK, MSG_UNROK, MSG_UNREG, MSG_JOIN, MSG_LEAVE, MSG_SEARCH, MSG_SEARCHOK, MSG_ERROR};

class RegMessage  extends Message{
	String username;
	public RegMessage(String username){
		this.type=MsgType.MSG_REG;
		this.username= username;
		ip_to=Config.bootstrap_ip;
		port_to=Config.bootstrap_port;
	}
	public String toString() {
		String temp=" REG " + ip_from + " " + port_from + " "+ username;
		return getLength(temp)+temp;
	}
}

class RegResult  extends Message{
	public String ip1, ip2;
	public int  port1,  port2;
	public int num_neighbors;
	
	public RegResult(String msg){
		type=MsgType.MSG_REGOK;
		
		String[] tokens=msg.split(" ");
		num_neighbors=Integer.parseInt(tokens[0]);
		if(num_neighbors>2) return;
		if(num_neighbors>0){
			ip1=tokens[1];
			port1=Integer.parseInt(tokens[2]);
		}if(num_neighbors>1){
			ip2=tokens[4];
			port2=Integer.parseInt(tokens[5]);
		}
		ip_from=Config.bootstrap_ip;
		port_from=Config.bootstrap_port;
	}
}

class UnRegMessage extends Message{
	String username;

	public UnRegMessage(String username){
		this.username=username;
		this.type=MsgType.MSG_UNREG;
		ip_to=Config.bootstrap_ip;
		port_to=Config.bootstrap_port;
	}

	public String toString(){
		String temp=" UNREG " + ip_from + " " + port_from + " "+ username;
		return getLength(temp)+temp;
	}
}

class UnRegResult extends Message{
	public int status;

	public UnRegResult(String msg){
		type=MsgType.MSG_UNROK;
		String[] tokens=msg.split(" ");
		status=Integer.parseInt(tokens[0]);
	}

}

class QueryFileMessage extends Message{
	public QueryFileMessage(String fileName, String ip, String port){
		
	}
}

class ReplyQueryFileMessage extends Message{
	
	public ReplyQueryFileMessage(String fileName, String ip, String port){
		
	}
}

class ForwardQueryFileMessage extends Message{
	public ForwardQueryFileMessage(){
	}
}