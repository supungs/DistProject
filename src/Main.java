import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	public static void main(String[] args) {
//		Config.my_username="useral1";
//		Config.bootstrap_ip="127.0.0.1";
//		Config.bootstrap_port=9999;
//		Config.my_ip="127.0.0.1";
//		Config.my_port=10001;
		
		if(args.length<5){
			System.out.println("Error in arguments");
			System.out.println("Example usage: BS_IP BS_PORT NODE_NAME NODE_IP NODE_PORT");
			System.exit(1);
		}
		Config.bootstrap_ip=args[0];
		Config.bootstrap_port=Integer.parseInt(args[1]);
		Config.my_username=args[2];
		Config.my_ip=args[3];
		Config.my_port=Integer.parseInt(args[4]);
		
		FileSharer sharer=new FileSharer();

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			try {
				String cmd=inFromUser.readLine();
				if(cmd.equals("exit")) { sharer.endSharing(); System.exit(1); }
				else if(cmd.equals("print files")) sharer.printMyFiles();
				else if(cmd.equals("print routes")) sharer.printMyRoutes();
				else if(cmd.equals("print neighbors")) sharer.printMyNeighbors();
				else if(cmd.equals("search random")) sharer.searchFile(Config.getRandomQuery());
				else if(cmd.substring(0,6).equals("search")) sharer.searchFile(cmd.substring(7));
				else System.out.println(">>Unknown command");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
}
