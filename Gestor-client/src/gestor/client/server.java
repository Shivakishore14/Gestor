package gestor;

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
			System.out.println("Server ready...");
			while(true) {
				cs = ss.accept();
				System.out.println("Accepted");
				clientInstance cI = new clientInstance(cs);
				cI.fun();
				cI.run();
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
	public void fun(){
		System.out.println("fun");
	}
	public void run() {
		try {
			System.out.println("Client connected");
			BufferedOutputStream out = new BufferedOutputStream(cs.getOutputStream());
			byte[] cmd = "message from server \n".getBytes();
			out.write(cmd, 0, cmd.length);
			out.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
