import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * This is class represents the top of intermediate layer.
 * Application layer directly invoke its functionalities.
 */
public class FileSharer {
	List<Neighbor> neighbors;
	Messenger messenger;
	HashMap<String,Video> fileListMap;
	//list of files to be shared and list of downloaded files.
	public FileSharer(){
		neighbors=new ArrayList<Neighbor>();		
		messenger=new Messenger();
		fileListMap=new HashMap<String,Video>();
		
		RegResult response=(RegResult) messenger.sendMessage(new RegMessage(Config.my_username));
		if(response.num_neighbors>2){
			System.out.println("Server refused registration.");
			System.exit(1);
		}
		if(response.num_neighbors>0){
			neighbors.add(new Neighbor(response.ip1, response.port1));
		}if(response.num_neighbors>1){
			neighbors.add(new Neighbor(response.ip2, response.port2));
		}

		UnRegResult unRegResponse=(UnRegResult) messenger.sendMessage(new UnRegMessage(Config.my_username));
		if(unRegResponse.status==0){
			System.out.println("Unregister is ok");
		}
	}
	
	//Sending the query message
	 public void sendQuery(Message msg){
		 QueryResponseMessage response=(QueryResponseMessage)messenger.sendMessage(msg);
		 if(response.no_file>=1){
			 System.out.println("Connect to the node with minimum number of hops");
		 }
		 else if(response.no_file==0){
			System.out.println("No files found");
		 }
		 else if(response.no_file==9999){
			System.out.println("Failure due to node unreachable");
		 }
		 else if(response.no_file==9998){
			System.out.println("Some other error");
		 }
	 }
	
	public void sendQueryMessage(Message msg){
		QueryMessage qmsg=(QueryMessage)msg;
		System.out.print("sendQuery Message");
		messenger.sender.sendUDP(qmsg.toString(),"127.0.0.1" , 10001);
	}
	
    //Set the Index of the list of files 
	public void setListOfFiles(String fileName){
		fileListMap.put(fileName, new Video());
	}
	
	//Get the list of files
	public ArrayList<String> containsFile(String fileName){
		ArrayList<String> output = new ArrayList<String> ();
			for(String s:fileListMap.keySet()){
				if(s.toLowerCase().contains(fileName.toLowerCase())){
					output.add(s);
				}
			}
			
			return output;
		
	}
}

class Neighbor{
	public String ip;
	public int port;
	public Neighbor(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
}

class Video{
	public int size;
	public int duration;
	
}
