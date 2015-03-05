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

enum MsgType {
	MSG_REG, MSG_REGOK, MSG_UNROK, MSG_UNREG, MSG_JOIN, MSG_LEAVE, MSG_SEARCH, MSG_SEARCHOK, MSG_ERROR, MSG_SER, MSG_SEROK
};

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
		if (num_neighbors > 2)
			return;
		if (num_neighbors > 0) {
			ip1 = tokens[1];
			port1 = Integer.parseInt(tokens[2]);
		}
		if (num_neighbors > 1) {
			ip2 = tokens[4];
			port2 = Integer.parseInt(tokens[5]);
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
	public int hops = 0;

	/*
	 * used when initiate a query for a file
	 */
	public QueryMessage(String file_name, String ip, int port, int hops) {
		this.type = MsgType.MSG_SER;
		this.file_name = file_name;
		this.ip_to = ip;
		this.port_to = port;
		this.hops = hops;
	}

	//129.82.62.142 5070 "Lord of the rings"
	public QueryMessage(String msg) {
		type = MsgType.MSG_SEARCH;

		String[] tokens = msg.split(" ");
		ip_from = tokens[0];
		port_from = Integer.parseInt(tokens[1]);
		tokens = msg.split("\"");
		file_name=tokens[1];
		hops=Integer.parseInt(tokens[2].substring(1));
	}
	
	/*
	 * used when forwarding a query for a file
	 */
	public QueryMessage(Message queryMessage) {
		QueryMessage message = (QueryMessage) queryMessage;
		// message.hops=message.hops+1;
		this.type = message.getType();
		this.file_name = message.file_name;
		this.ip_from = message.ip_from;
		this.port_from = message.port_from;
		this.hops = message.hops + 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Message#toString() length SER IP port file_name hops
	 */
	public String toString() {
		String temp = " SER " + ip_from + " " + port_from + " \""
				+ this.file_name + "\" " + this.hops;
		return getLength(temp) + temp;
	}
}

class QueryResponseMessage extends Message {

	public int no_file = 0;
	public String[] files;
	public int hops = 0;

	/*
	 * length SEROK no_files IP port hops filename1 filename2 ... ...
	 */
	public QueryResponseMessage(String ip, int port, int no_files,
			String[] fileNames) {
		this.type = MsgType.MSG_SEROK;
		this.ip_from = ip;
		this.port_from = port;
		this.files = fileNames;
		this.num_files = no_files;
		this.hops = hops;
	}

	//3 129.82.128.1 2301 baby_go_home.mp3 baby_come_back.mp3 baby.mpeg
	public QueryResponseMessage(String msg) {
		type = MsgType.MSG_SEARCHOK;

		String[] tokens = msg.split(" ");
		no_file=Integer.parseInt(tokens[0]);
		if(no_file>=1){
		ip_from = tokens[1];
		port_from =Integer.parseInt(tokens[2]);
		hops =Integer.parseInt(tokens[3]);
		
		for(int i=4;i<tokens.length;i++){
			files[i-4]=tokens[i];
		}	
		}
	}

	public String toString() {
		String file_names = files[0];
		for (int i=1;i<files.length; i++) {
			file_names += " " + files[i];
		}
		String temp = " SEROK " + num_files + " " + ip_from + " " + port_from
				+ " " + this.num_files + " " + file_names;
		return getLength(temp) + temp;
	}
}

