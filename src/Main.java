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
//		sender.sendUDP("0049 SER 129.82.62.142 5070 \"Lord of the rings\" 2", "127.0.0.1", 10001);
//		sender.sendUDP("0114 SEROK 3 129.82.128.1 2301 baby_go_home.mp3 baby_come_back.mp3 baby.mpeg", "127.0.0.1", 10001);
		

	}

}
