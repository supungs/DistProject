import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	public static void main(String[] args) {
		Config.my_username="useral2";
		Config.bootstrap_ip="127.0.0.1";
		Config.bootstrap_port=9999;
		Config.my_ip="127.0.0.1";
		Config.my_port=10002;
		
		FileSharer sharer=new FileSharer();

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			try {
				String cmd=inFromUser.readLine();
				if(cmd.equals("exit")) System.exit(1);
				else if(cmd.equals("print files")) sharer.printMyFiles();
				else if(cmd.equals("print neighbors")) sharer.printMyNeighbors();
				else if(cmd.equals("search random")) sharer.searchFile(Config.getRandomQuery());
				else if(cmd.substring(0,6).equals("search")) sharer.searchFile(cmd.substring(7));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
}
