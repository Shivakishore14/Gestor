package gestor.server;

import java.net.*;
import java.io.*;
import java.util.*;

public class server {
	public static void main(String args[]) {
		ServerSocket ss = null;
		Socket cs ;
		PrintStream ps;
		BufferedReader dis;
		try{
			ss = new ServerSocket(9005);
			System.out.println("Server system ready...");
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
	    return recv;
        }
	public void run() {
		try {
			BufferedOutputStream out = new BufferedOutputStream(cs.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			String sysName = in.readLine();
			byte[] info = "Success\n".getBytes();
			out.write(info, 0, info.length);
			out.flush();
			String ip = cs.getRemoteSocketAddress().toString();
			util.newClient(ip, sysName);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
