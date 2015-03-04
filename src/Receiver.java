import java.net.*; 
import java.io.*; 

public class Receiver extends Thread { 
	ServerSocket listener;
	Messenger messenger;
	
	public Receiver(int port, Messenger msgr) throws IOException{
		listener = new ServerSocket(port);
		this.messenger=msgr;
		start();
	}

	public void run(){
		try {
            while (true) {
                Socket socket = listener.accept();
                try {
                	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out =new PrintWriter(socket.getOutputStream(), true);
                    char[] buf=new char[100];
            	    in.read(buf);
            	    out.print("0013 RECIEVED");
            	    out.close();
            		in.close();
            		messenger.onReceive(buf);
                } finally {
                    socket.close();
                }
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
        finally {
            try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
}