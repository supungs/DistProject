public class Message {
	public MsgType type;
	public String ip_from, ip_to;
	public int port_from, port_to;
	public String username;

	public Message() {
		ip_from = Config.my_ip;
		port_from = Config.my_port;
	}

	public String toString() {
		return "";
	}

	public MsgType getType() {
		return type;
	}

	protected static String getLength(String msg) {
		int len = msg.length() + 4;
		return String.format("%04d", len);
	}
}

enum MsgType { MSG_REG, MSG_REGOK, MSG_UNROK, MSG_UNREG, MSG_JOINOK ,MSG_JOIN, MSG_LEAVE,MSG_LEAVEOK ,MSG_SER, MSG_SEROK, MSG_ERROR};

class RegMessage extends Message {
	String username;

	public RegMessage(String username) {
		this.type = MsgType.MSG_REG;
		this.username = username;
		ip_to = Config.bootstrap_ip;
		port_to = Config.bootstrap_port;
	}

	public String toString() {
		String temp = " REG " + ip_from + " " + port_from + " " + username;
		return getLength(temp) + temp;
	}
}

class RegResult extends Message {
	public String ip1, ip2;
	public int port1, port2;
	public int num_neighbors;

	public RegResult(String msg) {
		type = MsgType.MSG_REGOK;

		String[] tokens = msg.split(" ");
		num_neighbors = Integer.parseInt(tokens[0]);
		
		if (num_neighbors > 0) {
			int rand=(int) Math.floor(Math.random()*num_neighbors);
			ip1 = tokens[3*rand+1];
			port1 = Integer.parseInt(tokens[3*rand+2]);
		}
		if (num_neighbors > 1) {
			while(true){
				int rand=(int) Math.floor(Math.random()*num_neighbors);				
				ip2 = tokens[3*rand+1];
				port2 = Integer.parseInt(tokens[3*rand+2]);
				if(ip1.equals(ip2) && port1==port2) continue;
				else break;
			}
			num_neighbors=2;
		}
		
		ip_from = Config.bootstrap_ip;
		port_from = Config.bootstrap_port;
	}
}

class UnRegMessage extends Message {
	String username;

	public UnRegMessage(String username) {
		this.username = username;
		this.type = MsgType.MSG_UNREG;
		ip_to = Config.bootstrap_ip;
		port_to = Config.bootstrap_port;
	}

	public String toString() {
		String temp = " UNREG " + ip_from + " " + port_from + " " + username;
		return getLength(temp) + temp;
	}
}

class UnRegResult extends Message {
	public int status;

	public UnRegResult(String msg) {
		type = MsgType.MSG_UNROK;
		String[] tokens = msg.split(" ");
		status = Integer.parseInt(tokens[0]);
	}

}

/**
 * 
 * @author Thamayanthy
 * @author Fahima
 * @param file_name
 */

class QueryMessage extends Message {

	public String file_name = "";
	public int hops, sequence;
	/*
	 * used when initiate a query for a file
	 */
	public QueryMessage(String file_name, String ip, int port, int seq, int hops) {
		this.type = MsgType.MSG_SER;
		this.file_name = file_name;
		this.ip_to = ip;
		this.port_to = port;
		this.sequence=seq;
		this.hops = hops;
	}

	//129.82.62.142 5070 "Lord of the rings" 12 4
	public QueryMessage(String msg) {
		type = MsgType.MSG_SER;

		String[] tokens = msg.split(" ");
		ip_from = tokens[0];
		port_from = Integer.parseInt(tokens[1]);
		tokens = msg.split("\"");
		file_name=tokens[1];
		sequence=Integer.parseInt(tokens[2].split(" ")[1]);
		hops=Integer.parseInt(tokens[2].split(" ")[2]);
	}
	
	/*
	 * used when forwarding a query for a file
	 */
	public QueryMessage(QueryMessage message, String ipto, int portto) {
		this.type = message.getType();
		this.file_name = message.file_name;
		this.ip_from = message.ip_from;
		this.port_from = message.port_from;
		this.sequence=message.sequence;
		this.hops = message.hops + 1;
		this.ip_to=ipto;
		this.port_to=portto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Message#toString() length SER IP port file_name hops
	 */
	public String toString() {
		String temp = " SER " + ip_from + " " + port_from + " \""
				+ this.file_name + "\" " + sequence+ " "+ hops;
		return getLength(temp) + temp;
	}
}

class QueryResponseMessage extends Message {

	public int no_file = 0;
	public String[] files;
	public int hops, sequence;

	public QueryResponseMessage(QueryMessage qmsg, String[] files){
		this.ip_to=qmsg.ip_from;
		this.port_to=qmsg.port_from;
		this.sequence=qmsg.sequence;
		this.hops=qmsg.hops+1;
		
		this.files=new String[files.length];
		for(int i=0;i<files.length;i++){
			this.files[i]=files[i].replace(' ', '_');
		}
		no_file=files.length;
	}
	
	//no_files IP port seq hops filename1 filename2
	public QueryResponseMessage(String msg) {
		type = MsgType.MSG_SEROK;

		String[] tokens = msg.split(" ");
		no_file=Integer.parseInt(tokens[0]);
		ip_from = tokens[1];
		port_from =Integer.parseInt(tokens[2]);
		sequence =Integer.parseInt(tokens[3]);
		hops =Integer.parseInt(tokens[4]);
		if(no_file>=1){		
			files=new String[no_file];
			for(int i=5;i<tokens.length;i++){
				files[i-5]=tokens[i].replace('_', ' ');
			}	
		}
	}
	public String toString() {
		String fileNames="";
		for(int i=0;i<files.length;i++){
			fileNames+=files[i];
			fileNames+=" ";
		}
		String temp = " SEROK " + no_file + " " + ip_from+ " " + port_from + " " + sequence+" " + hops+" "+fileNames;
		return getLength(temp) + temp;
	}
	
}

class JoinMessage extends Message{
	public JoinMessage(String ip_to, int port_to){
        this.type=MsgType.MSG_JOIN;
		this.ip_to=ip_to;
		this.port_to=port_to;
	}
	public JoinMessage(String msg){
        this.type=MsgType.MSG_JOIN;
        String[] tokens = msg.split(" ");
		this.ip_from=tokens[0];
		this.port_from=Integer.parseInt(tokens[1]);
	}
	public String toString(){
		String temp=" JOIN " + ip_from + " " + port_from;
		return getLength(temp)+temp;
	}
}

class JoinResult extends Message{
	public int status;
	public JoinResult(String ip_to, int port_to){
        this.type=MsgType.MSG_JOINOK;
		this.ip_to=ip_to;
		this.port_to=port_to;
	}
	
	public JoinResult(String msg){
        type=MsgType.MSG_JOINOK;
		String[] tokens=msg.split(" ");
		status=Integer.parseInt(tokens[0]);
	}
	public String toString(){
		String temp=" JOINOK " + status;
		return getLength(temp)+temp;
	}
}

class LeaveMessage extends Message{
	public LeaveMessage(String ip_to, int port_to){
		this.type=MsgType.MSG_LEAVE;
		this.ip_to=ip_to;
		this.port_to=port_to;
	}
	public LeaveMessage(String msg){
		type=MsgType.MSG_LEAVE;
		String[] tokens=msg.split(" ");
		ip_from=tokens[0];
		port_from=Integer.parseInt(tokens[1]);
	}
	public String toString(){
		String temp=" LEAVE " + ip_from + " " + port_from;
		return getLength(temp)+temp;
	}
}

class LeaveResult extends Message{
	public int status;

	public LeaveResult(String msg){
		type=MsgType.MSG_LEAVEOK;
		String[] tokens=msg.split(" ");
		status=Integer.parseInt(tokens[0]);
	}
}
