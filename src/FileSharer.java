import java.util.ArrayList;
import java.util.List;

/*
 * This is class represents the top of intermediate layer.
 * Application layer directly invoke its functionalities.
 */
public class FileSharer {
	List<Neighbor> neighbors;
	Messenger messenger;
	//list of files to be shared and list of downloaded files.
	public FileSharer(){
		neighbors=new ArrayList<Neighbor>();		
		messenger=new Messenger();
		
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
		
		if(neighbors.size()>0){
			QueryMessage qmsg=new QueryMessage("Lord of the rings", neighbors.get(0).ip,  neighbors.get(0).port, 4);
			messenger.sendMessage(qmsg);
		}
	}
	
	public void sendQueryMessage(Message msg){
		QueryMessage qmsg=(QueryMessage)msg;
		System.out.print("sendQuery Message");
		messenger.sender.sendUDP(qmsg.toString(),"127.0.0.1" , 10001);
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
