package gestor.client;


import java.net.*;
import java.io.*;
import java.util.*;

public class server1 {
	public static void main(String args[]) {
		ServerSocket ss = null;
		Socket cs ;
		PrintStream ps;
		BufferedReader dis;
		
		
		try	{
			ss = new ServerSocket(9007);
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
		String cmd;
		String val;
		
		try {
			String a[] = recv.split("::");
			if (a[0].equals("0")){//remote desktop
				String ip = a[1];
				new ClientInitiator(ip,9008).start();			
			}
			if (a[0].equals("1")){
				//code to shutdown i.e logoff current user (1::)	
				cmd = "shutdown";
			}
			if (a[0].equals("2")){
				//code to shutdown with time(2::<<time>>)
				val = "shutdown -t";
				cmd = val+" "+a[1];
			}
			if (a[0].equals("3")){
				//code to reboot
				cmd = "shutdown -r";
			}
			if (a[0].equals("4")){
				//code to execute proxy (netsh winhttp set proxy ProxyName:80)(4::<<ip>>)
				val = "netsh winhttp set proxy";
				cmd = val+" "+a[1]+":80";
			}
			if (a[0].equals("5")){//code to reset proxy
				cmd = "netsh winhttp reset proxy";
			}
			if (a[0].equals("6")){ // code to enable firewall
				cmd = "netsh advfirewall set currentprofile state on";
			}
			if (a[0].equals("7")){ // code to disable firewall
				cmd = "netsh advfirewall set currentprofile state off";
			}
			if (a[0].equals("8")){ //(8::<<username>>::<<password>>)
				val = "net user /add";
				cmd = val+" "+a[1]+" "+a[2];
			}
			if (a[0].equals("9")){  //(9::<<username>>)
				val = "net user /delete";
				cmd = val+" "+a[1];
			}
			if (a[0].equals("10")){ //(10::<<username>>::<<password>>)
				val = "net user";
				cmd = val+" "+a[1]+" "+a[2];
			}
			
		}catch (Exception e){ e.printStackTrace(); }
        	return recv;
        }
	private void executecmd(String cmd) {
		BufferedInputStream procStdout;
		Process proc;
		try {
			proc = Runtime.getRuntime().exec(new String(cmd));
              		procStdout = new BufferedInputStream(proc.getInputStream());
               		procStdout.close();
		}
		catch(Exception e) {
			System.out.println("the exception is:"+e);
		}	
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
			cs.close();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
