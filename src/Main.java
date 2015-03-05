
public class Main {

	public static void main(String[] args) {
		Config.my_username="user";
		Config.bootstrap_ip="127.0.0.1";
		Config.bootstrap_port=9999;
		Config.my_ip="127.0.0.1";
		Config.my_port=10001;
		
		FileSharer sharer=new FileSharer();
	}

}
