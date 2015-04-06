
public class CircularArray {
	int[] items;
	int cnt, length;

	public CircularArray(int len){
		items=new int[len];
		length=len;
		cnt=0;
	}

	public void add(QueryMessage msg){
		int nxtSlot=cnt % length;
		items[nxtSlot]=getHash(msg);
		cnt++;
	}
	
	public void add(String query, int seq){
		QueryMessage msg=new QueryMessage(query, "", 1, seq, 0);
		int nxtSlot=cnt % length;
		items[nxtSlot]=getHash(msg);
		cnt++;
	}
	
	private int getHash(QueryMessage msg){
		String str=msg.ip_from+" "+msg.port_from+" "+msg.file_name+ " "+ msg.sequence;
		int hash=7;
		for (int i=0; i < str.length(); i++) {
		    hash = hash*31+str.charAt(i);
		}
		return hash;
	}
	
	public boolean contains(QueryMessage msg){
		int hash=getHash(msg);
		for (int i=0; i < length; i++) {
			if(items[i]==hash) return true;
		}
		return false;
	}
}
