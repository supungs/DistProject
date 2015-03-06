import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {

	public static void main(String[] args) {
		Config.my_username="useral1";
		Config.bootstrap_ip="127.0.0.1";
		Config.bootstrap_port=9999;
		Config.my_ip="127.0.0.1";
		Config.my_port=10001;
		
		FileSharer sharer=new FileSharer();
		
		
		
		//		QueryMessage msg=new QueryMessage("Lord of the rings", "127.0.0.1", 10001, 4);
		//		Sender sender=new Sender();
		//		sender.sendUDP(msg.toString(), msg.ip_to, msg.port_to);
		//Adding files to the node
		int numOfFiles=4;
		String[] fileNames={"Adventures of Tintin","Jack and Jill","Glee","Harry Potter"};
		for(int i=0;i<numOfFiles;i++){
		sharer.setListOfFiles(fileNames[i]);
		}
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		try {
			inFromUser.readLine();
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
		//Initially sending query message to the neighbors by original file
		for(int i=0;i<sharer.neighbors.size();i++){
		QueryMessage msg=new QueryMessage("Adventures of Tintin", sharer.neighbors.get(i).ip,sharer.neighbors.get(i).port,0);
		sharer.sendQuery(msg);
		System.exit(1);
	}
	}
}
