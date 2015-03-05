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
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		try {
			inFromUser.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.exit(1);
		
//		QueryMessage msg=new QueryMessage("Lord of the rings", "127.0.0.1", 10001, 4);
//		Sender sender=new Sender();
//		sender.sendUDP(msg.toString(), msg.ip_to, msg.port_to);
	}

}
