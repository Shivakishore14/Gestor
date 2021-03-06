package gestor.client;


import java.net.*;
import java.io.*;
import java.nio.CharBuffer;
import java.util.*;

public class server1 extends Thread{
	public static int port = 1111;
	public void run() {
		ServerSocket ss = null;
		Socket cs ;
		PrintStream ps;
		BufferedReader dis;
		
		
		try	{
			ss = new ServerSocket(9007);
			while(true) {
				cs = ss.accept();
				clientInstance1 cI = new clientInstance1(cs);
				cI.start();
			}
		} catch(IOException e) {
			System.out.println("the exception is:"+e);
		}
	}
}
class clientInstance1 extends Thread {
	
	protected Socket cs;

	public String getStoredData(String choice) {
		String temp = "";
		File file = new File("server.cfg");

                try(Scanner sc = new Scanner(file)) {
                	while(sc.hasNextLine()) {
                        	temp = sc.nextLine();

                                if(!temp.startsWith("#") && temp.split("=")[0].equals(choice)) {
                                        temp = temp.split("=")[1];
                                        break;
                                }
                        }
                } catch(IOException e) {
                	e.printStackTrace();
                }

		return temp;
	}
	public clientInstance1(Socket s) {
		cs = s;
	}
	String fname="";
        private String process(String recv){
        	System.out.println(recv);
			String cmd;
			String val;
			fname = recv.replaceAll("::", "_");
		try {
			String a[] = recv.split("::");
			if (a[0].equals("Y0")){
				//code to execute proxy (netsh winhttp set proxy ProxyName:80)(4::<<ip>>)
				//val = "C:\\Windows\\System32\\cmd.exe /c netsh winhttp set proxy";
				val = "reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyServer /t REG_SZ /d";
				// proxyserveraddress:proxyport /f"
				cmd = val+" "+a[1]+":80 /f";
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y1")){//code to reset proxy
				cmd = "reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d 0 /f";
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y2")){ // code to enable firewall
				cmd = "C:\\Windows\\System32\\cmd.exe /c netsh advfirewall set currentprofile state on";
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y3")){ // code to disable firewall
				cmd = "C:\\Windows\\System32\\cmd.exe /c netsh advfirewall set currentprofile state off";
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y4")){ //(8::<<username>>::<<password>>)
				val = "C:\\Windows\\System32\\cmd.exe /c net user /add";
				cmd = val+" "+a[1]+" "+a[2];
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y5")){  //(9::<<username>>)
				val = "C:\\Windows\\System32\\cmd.exe /c net user /delete";
				cmd = val+" "+a[1];
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y16")){ //(10::<<username>>::<<password>>)
				val = "C:\\Windows\\System32\\cmd.exe /c net user";
				cmd = val+" "+a[1]+" "+a[2];
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y6")){ // code to display status of driver
				cmd = "C:\\Windows\\System32\\cmd.exe /c wmic diskdrive get status";
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y7")){ // code to show ip
				cmd = "C:\\Windows\\System32\\cmd.exe /c ipconfig";
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y8")){ // code to display netstat
				cmd = "C:\\Windows\\System32\\cmd.exe /c netstat -n";
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y9")){ //chkdsk <<drive_name>>
				val = "C:\\Windows\\System32\\cmd.exe /c chkdsk";
				cmd = val+""+a[1];
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y10")){ //chkntfs <<drive_name>>
				val = "C:\\Windows\\System32\\cmd.exe /c chkntfs";
				cmd = val+""+a[1];
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y11")){ //scan
				cmd = "C:\\Windows\\System32\\cmd.exe /c sfc /SCANNOW";
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y12")){ //driver query
				cmd = "C:\\Windows\\System32\\cmd.exe /c driverquery";
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y13")){ //systeminfo
				cmd = "C:\\Windows\\System32\\cmd.exe /c systeminfo";
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y14")){ // label <<drive_name>>
				val = "C:\\Windows\\System32\\cmd.exe /c label";
				cmd = val+""+a[1];
				String result = executeCmdWithResult(cmd);
				return result;
			}
			if (a[0].equals("Y15")){ // compact
				cmd = "C:\\Windows\\System32\\cmd.exe /c compact";
				String result = executeCmdWithResult(cmd);
				return result;
			}
			
		}catch (Exception e){ e.printStackTrace(); }
        	return recv;
    }
		
	private void processNoResult(String recv){
	String cmd;
		String val;
		
		try {
			String a[] = recv.split("::");
			if (a[0].equals("N0")){//remote desktop
				String ip =  getStoredData("ip");
				
				new ClientInitiator(ip,9008).start();		
			}
			if (a[0].equals("N1")){
				//code to shutdown i.e logoff current user (01::)	
				cmd = "cmd /c shutdown -p";
				executeCmd(cmd);
			}
			if (a[0].equals("N2")){
				//code to shutdown with time(2::<<time>>)
				val = "cmd /c shutdown -t";
				cmd = val+" "+a[1];
				executeCmd(cmd);
			}
			if (a[0].equals("N3")){
				//code to reboot
				cmd = "cmd /c shutdown -r";
				executeCmd(cmd);
			}
			if (a[0].equals("N4")){
				cmd = "C:\\Gestor\\SHARE.EXE -dir="+a[1];
				executeCmd(cmd);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}
	private void executeCmd(String cmd) {
		System.out.println(cmd);
		BufferedReader procStdout;
		Process proc;
		String s = "";
		try {
					proc = Runtime.getRuntime().exec(cmd);
              		procStdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    while ((s = procStdout.readLine()) != null){
                            System.out.println(s);
                    }
					procStdout.close();
		}
		catch(Exception e) {
			System.out.println("the exception is:"+e);
		}	
	}
	private void fileShare(String dir) throws IOException{
		
		String execStr = "C:\\Gestor\\SHARE.EXE -dir="+dir;
        Process proc = Runtime.getRuntime().exec(execStr);
	}
	private String executeCmdWithResult(String cmd) {
	        BufferedReader procStdout;
	        Process proc;
			String result="";
	        try {
			     
					proc = Runtime.getRuntime().exec(cmd);
	                procStdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	                String s="";
					FileWriter f = new FileWriter(fname);
	                while ((s = procStdout.readLine()) != null){
	                        System.out.println(s);
							f.write(s+"\n");
							result += result + "\n";
	                }
					f.close();
	                procStdout.close();
	                return result;
	        }
	        catch(Exception e) {	
		            System.out.println("the exception is:"+e);
        	}  
        	return "";
 
	}
	public void run() {
		try {
			PrintWriter out = new PrintWriter(cs.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			String recv = in.readLine();
			if(recv.charAt(0) == 'Y'){
					String result = process(recv);
					FileReader r = new FileReader(fname);
					CharBuffer c = CharBuffer.allocate(50000);
					r.read(c);
					out.write(c.array());
					out.flush();
			} else {
					processNoResult(recv);
			}
			
			cs.close();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
