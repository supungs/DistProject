import java.net.*; 
import java.io.*; 

public class Receiver extends Thread { 
	DatagramSocket listener;
	Messenger messenger;
	
	public Receiver(int port, Messenger msgr) throws IOException{
		listener = new DatagramSocket(port);
		this.messenger=msgr;
		start();
	}

	public void run(){
		try {		
            while (true) {
            	System.out.println("Waiting for Incomming..");
            	byte[] receiveData = new byte[300]; 
                byte[] sendData  = new byte[300]; 
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 

                listener.receive(receivePacket); 
                String msg = new String(receivePacket.getData());
                messenger.onReceive(msg);
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
}