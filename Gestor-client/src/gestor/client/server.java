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
			ss = new ServerSocket(4444);
			System.out.println("Slave system ready...");
			while(true) {
				cs = ss.accept();
				System.out.println("Accepted");
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
        
            return recv;
        }
	public void run() {
		try {
			BufferedOutputStream out = new BufferedOutputStream(cs.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			byte[] cmd = "YYYSSddd uniq id\n".getBytes();
			out.write(cmd, 0, cmd.length);
			out.flush();
			String recv = in.readLine();
			String result = process(recv);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
