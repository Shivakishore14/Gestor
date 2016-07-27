package gestor.server;

import java.net.*;
import java.io.*;
import java.util.*;

class client{
	public ArrayList<String> findAll() {
		Socket cs = null;		
		Process proc;
		ArrayList<String> ipaddress = new ArrayList<String>();
		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress ia = (InetAddress) ee.nextElement();
					String ip = ia.getHostAddress();
					System.out.println(ip);
					if(ip.indexOf('.') == -1)
					    continue;
					String subnet = getSubnet(ip);
					
					for (int i=1;i<255;i++){
						String host=subnet + "." + i;
						try {
							cs = new Socket(host, 4444);
							BufferedReader in= new BufferedReader(new InputStreamReader(cs.getInputStream()));
							BufferedOutputStream out = new BufferedOutputStream(cs.getOutputStream());
							System.out.println("ip--> " + cs.getInetAddress().getHostAddress());
							String id = in.readLine();
							System.out.println("unique id is:"+ id);
							String cmd="cmd";
							out.write(cmd.getBytes());
							out.flush();
							util.newClient(ip, id);
							ipaddress.add(ip);
						} catch (SocketException se){
						} finally {
							if ( cs != null)
								cs.close();
						}
					}
				}
			}
		} catch(IOException e) {
			System.out.println("the exception is:"+e);
		}
		return ipaddress;
	}
	private String getSubnet(String ip){
		String subnet;
		String[] a = ip.split(" . ");
		for (String i : a)
		    System.out.println(i);
		//subnet = a[0] + "." + a[1] + "." + a[2];
		return ip;
	}
	public static void main(String args[]){
		client c = new client();
		c.findAll();
	}
}
