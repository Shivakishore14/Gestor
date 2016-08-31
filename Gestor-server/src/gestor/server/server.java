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
		util.truncate();
		try{
			new ServerInitiator(9008).start(); //starts remote desktop
			ss = new ServerSocket(9006);
			System.out.println("Server system ready...");
			while(true) {
				cs = ss.accept();
				clientInstance cI = new clientInstance(cs);
				cI.start();
			}
		} catch(IOException e) {
			System.out.println("the exception is:"+e);
		}finally{
			try{
			        ss.close();
			}catch(IOException e){}
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
			String a[] = ip.split(":");
			System.out.print(a[0]);
			ip = a[0].substring(1);
			util.newClient(ip, sysName);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
