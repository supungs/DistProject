import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/*
 * This is class represents the top of intermediate layer.
 * Application layer directly invoke its functionalities.
 */
public class FileSharer{
	List<Node> neighbors;
	Messenger messenger;
	String[] myFiles;
	HashMap<String, Node> fileListMap;
	Node myNode;
	
	public FileSharer() {
		myNode=new Node(Config.my_ip, Config.my_port);
		
		neighbors = new ArrayList<Node>();
		messenger = new Messenger(this);
		fileListMap = new HashMap<String, Node>();
		myFiles=Config.getRandomFiles();
		initFileMap();
		
		register();
		greetNeighbors();
	}
	
	private void initFileMap(){
		for(String f:myFiles){
			fileListMap.put(f, myNode);
		}
	}
	private void register(){
		RegResult response = (RegResult) messenger.sendMessage(new RegMessage(Config.my_username));
		if (response.num_neighbors > 2) {
			System.out.println("Server refused registration.");
			System.exit(1);
		}
		if (response.num_neighbors > 0) {
			neighbors.add(new Node(response.ip1, response.port1));
		}
		if (response.num_neighbors > 1) {
			neighbors.add(new Node(response.ip2, response.port2));
		}		
	}
	
	private void greetNeighbors(){
		for(int i=0;i<neighbors.size();i++){
			Node nebr=neighbors.get(i);
			JoinMessage msg=new JoinMessage(nebr.ip,nebr.port);
			messenger.sendMessage(msg);
		}
	}

	public void searchFile(String query) {	
		ArrayList<String> localFiles =containsFile(query);
		if(localFiles.size()>0){
			System.out.println(">>Files found localy:");
			System.out.println(localFiles.toString());
		}else{
			System.out.println(">>No Files found localy: Seaching the network");
			for(Node node:neighbors){
				QueryMessage qmsg=new QueryMessage(query, node.ip, node.port, 3);
				messenger.sendMessage(qmsg);
			}
		}
	}
	
	public void onPeerRequest(Message msg){
		if(msg.getType()==MsgType.MSG_JOIN){
			neighbors.add(new Node(msg.ip_from, msg.port_from));
		}
	}
	

	// Sending the query message
	public void sendQuery(Message msg) {
		QueryResponseMessage response = (QueryResponseMessage) messenger
				.sendMessage(msg);
		if (response.no_file >= 1) {
			System.out
					.println("Connect to the node with minimum number of hops");
		} else if (response.no_file == 0) {
			System.out.println("No files found");
		} else if (response.no_file == 9999) {
			System.out.println("Failure due to node unreachable");
		} else if (response.no_file == 9998) {
			System.out.println("Some other error");
		}
	}

	public void sendQueryMessage(Message msg) {
		QueryMessage qmsg = (QueryMessage) msg;
		System.out.print("sendQuery Message");
		messenger.sender.sendUDP(qmsg.toString(), "127.0.0.1", 10001);
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

	public void printMyFiles(){
		System.out.println(">> My Files:");
		System.out.println(Arrays.toString(myFiles));
		System.out.println("");
	}
	
	public void printMyNeighbors(){
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

	public String toString(){
		return "<"+ip+":"+port+">";
	}
}

class Video {
	public String name;
	public int size;
	public int duration;
}