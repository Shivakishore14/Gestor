package gestor.client;

import java.net.*;
import java.io.*;
import java.util.*;

class clients{
	public static void main(String args[]) {
		Socket soc ;
		PrintStream ps;
		BufferedReader dis;
		String sdate;
		String cmd=null;
		Process proc;
		try {
			soc = new Socket("127.0.0.255",4444);
			dis= new BufferedReader(new InputStreamReader(soc.getInputStream()));
			sdate = dis.readLine();
			System.out.println("the date/time on server is:"+ sdate);
		} catch(IOException e) {
			System.out.println("the exception is:"+e);
		}
	}
}
