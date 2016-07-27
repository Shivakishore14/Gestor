package gestor.client;

import java.net.*;
import java.io.*;
import java.util.*;

class clients{
	public static void main(String args[]) {
		Socket cs ;		
		Process proc;
		try {
			cs = new Socket("127.0.0.255",4444);
			BufferedReader in= new BufferedReader(new InputStreamReader(cs.getInputStream()));
			BufferedOutputStream out = new BufferedOutputStream(cs.getOutputStream());
			String id = in.readLine();
			System.out.println("unique id is:"+ id);
			String cmd="cmd";
			out.write(cmd.getBytes());
			out.flush();
		} catch(IOException e) {
			System.out.println("the exception is:"+e);
		}
	}
}
