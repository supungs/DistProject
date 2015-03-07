import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
 * This is class represents the top of intermediate layer.
 * Application layer directly invoke its functionalities.
 */
public class FileSharer {
	List<Node> neighbors;
	Messenger messenger;
	String[] myFiles;
	HashMap<String, Node> fileListMap;
	Node myNode;
	int queryCounter, regAtempt;
	CircularArray recentQueries;
	
	public FileSharer() {
		myNode = new Node(Config.my_ip, Config.my_port);

		neighbors = new ArrayList<Node>();
		messenger = new Messenger(this);
		fileListMap = new HashMap<String, Node>();
		recentQueries=new CircularArray(10);
		
		myFiles = Config.getRandomFiles();
		initFileMap();

		register();
		greetNeighbors();
	}

	private void initFileMap() {
		for (String f : myFiles) {
			fileListMap.put(f, myNode);
		}
	}

	private void register() {
		RegResult response = (RegResult) messenger.sendMessage(new RegMessage(Config.my_username));
		if (response.num_neighbors==9998 && regAtempt<1) {
			UnRegMessage unreg=new UnRegMessage(Config.my_username);
			messenger.sendMessage(unreg);
			regAtempt++;
			register();
		}else if (response.num_neighbors ==1) {
			neighbors.add(new Node(response.ip1, response.port1));
		}else if (response.num_neighbors==2) {
			neighbors.add(new Node(response.ip1, response.port1));
			neighbors.add(new Node(response.ip2, response.port2));
		}else if (response.num_neighbors>2){
			if (response.num_neighbors==9997)
				System.out.println("Registered to another user, try a different IP and port");
			else if (response.num_neighbors==9996)
				System.out.println("Can’t register. BS full");
			else System.out.println("Unknwon error in registration");
			System.exit(1);
		}
	}

	private void greetNeighbors() {
		for (int i = 0; i < neighbors.size(); i++) {
			Node nebr = neighbors.get(i);
			JoinMessage msg = new JoinMessage(nebr.ip, nebr.port);
			messenger.sendMessage(msg);
		}
	}

	public void searchFile(String query) {
		ArrayList<String> localFiles = containsFile(query);
		if (localFiles.size() > 0) {
			System.out.println(">>Files found localy:");
			System.out.println(localFiles.toString());
		} else {
			System.out.println(">>No Files found localy: Seaching the network");
			queryCounter++;
			recentQueries.add(query, queryCounter);
			for (Node node : neighbors) {
				QueryMessage qmsg = new QueryMessage(query, node.ip, node.port,queryCounter,0);
				messenger.sendMessage(qmsg);
			}
		}
	}

	public void onPeerRequest(Message msg) {
		if (msg.getType() == MsgType.MSG_JOIN) {
			neighbors.add(new Node(msg.ip_from, msg.port_from));
		} else if (msg.getType() == MsgType.MSG_SER) {
			QueryMessage qmsg = (QueryMessage) msg;
			if(!recentQueries.contains(qmsg)){
				recentQueries.add(qmsg);
				ArrayList<String> localFiles = containsFile(qmsg.file_name);
				if (localFiles.size() > 0) {
					QueryResponseMessage qresp = new QueryResponseMessage(qmsg, localFiles.toArray(new String[localFiles.size()]));
					messenger.sendMessage(qresp);
				}else{
					forwardQuery(qmsg);
				}
			}
		} else if (msg.getType() == MsgType.MSG_SEROK) {
			QueryResponseMessage qresp = (QueryResponseMessage) msg;
			addToFileMap(qresp);
			System.out.println(">>Files found at <" + qresp.ip_from + ":" + qresp.port_from + "> :");
			System.out.println(Arrays.toString(qresp.files));
			
		}else if (msg.getType() == MsgType.MSG_LEAVE) {
			removeNeighbor(msg.ip_from, msg.port_from);
		}
	}

	private void removeNeighbor(String ip, int port){
		List<String> keys=new ArrayList<String>();
		for (Map.Entry<String, Node> entry : fileListMap.entrySet()) {
			String key = entry.getKey();
			Node n = entry.getValue();
			if(n.ip.equals(ip) && n.port==port){
				keys.add(key); 
			}
		}
		for(String k:keys)
			fileListMap.remove(k);
		
		for(int i=0;i< neighbors.size();i++){
			Node n=neighbors.get(i);
			if(n.ip.equals(ip) && n.port==port){
				neighbors.remove(i);
				break;
			}
		}
	}
	
	private void addToFileMap(QueryResponseMessage qresp) {
		Node owner=new Node(qresp.ip_from,qresp.port_from);
		boolean exist=false;
		for(Node n: neighbors){
			if(n.ip.equals(owner.ip) && n.port==owner.port){
				exist=true; break;
			}
		}
		if(!exist) neighbors.add(owner);
		
		for (String f : qresp.files) {
			fileListMap.put(f, owner);
		}		
	}

	// forward the query message
	public void forwardQuery(QueryMessage msg) {
		if(msg.hops+1==5) return;
		for (Node node : neighbors) {
			if(node.ip.equals(msg.ip_from) && node.port==msg.port_from) continue;
			QueryMessage qmsg = new QueryMessage(msg, node.ip, node.port);
			messenger.sendMessage(qmsg);
		}
	}

	// Get the list of files
	public ArrayList<String> containsFile(String fileName) {
		ArrayList<String> output = new ArrayList<String>();
		String searchwords[] = fileName.toLowerCase().split(" +");
		for (String file_from_list : fileListMap.keySet()) {
			String lowercase_file = file_from_list.toLowerCase();
			int available_words = 0;
			for (int i = 0; i < searchwords.length; i++) {
				String currentword = searchwords[i];
				if (lowercase_file.contains(currentword + " ")
						|| lowercase_file.contains(currentword + ".")
						|| lowercase_file.endsWith(currentword)) {
					available_words++;
				}
			}
			if (available_words == searchwords.length)
				output.add(file_from_list);
		}
		return output;
	}

	public void endSharing() {
		UnRegMessage unreg=new UnRegMessage(Config.my_username);
		messenger.sendMessage(unreg);
		for(Node n: neighbors){
			LeaveMessage lmsg=new LeaveMessage(n.ip, n.port);
			messenger.sendMessage(lmsg);
		}	
	}
	
	public void printMyFiles() {
		System.out.println(">> My Files:");
		System.out.println(Arrays.toString(myFiles));
		System.out.println("");
	}
	public void printMyRoutes() {
		System.out.println(">> My Routes:");
		System.out.println(fileListMap.toString());
		System.out.println("");
	}
	public void printMyNeighbors() {
		System.out.println(">> My Neighbors:");
		System.out.println(neighbors.toString());
		System.out.println("");
	}
}

class Node {
	public String ip;
	public int port;

	public Node(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public String toString() {
		return "<" + ip + ":" + port + ">";
	}
}