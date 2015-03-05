import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Sender {
	public char[] sendTo(String msg, String ip, int port){
		Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(ip, port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            out.println(msg);
    	    char[] buf=new char[100];
    	    in.read(buf);
    	    out.close();
    		in.close();
    		echoSocket.close();
    		System.out.println(out);
    		return buf;
        } catch (Exception e) {
        	e.printStackTrace();
            System.err.println("Couldn't connect to: " + ip);
            //System.exit(1);
        }
        return null;
	}
	
}
