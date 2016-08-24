package gestor.client;


import java.net.*;
import java.io.*;
import java.util.*;

public class server {
	public static void main(String args[]) {
		ServerSocket ss = null;
		Socket cs ;
		PrintStream ps;
		BufferedReader dis;
		try	{
			ss = new ServerSocket(9009);
			while(true) {
				cs = ss.accept();
				clientInstance cI = new clientInstance(cs);
				cI.start();
			}
		} catch(IOException e) {
			System.out.println("the exception is:"+e);
		}
	}
}
class clientInstance extends Thread {
	protected Socket cs;

	public clientInstance(Socket s) {
		cs = s;
	}
        private String process(String recv){
        	System.out.println(recv);
		try {
			String a[] = recv.split("::");
			if (a[0].equals("0"){
				String ip = a[1];
				new ClientInitiator(ip,9008).start();			
			}		
		}
        	return recv;
        }
	public void run() {
		try {
			BufferedOutputStream out = new BufferedOutputStream(cs.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			String recv = in.readLine();
			String result = process(recv);
			byte[] br = (result+"\n").getBytes();
			out.write(br, 0, br.length);
			out.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}\
