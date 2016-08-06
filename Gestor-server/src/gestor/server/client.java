package gestor.server;

import java.net.*;
import java.io.*;
import java.util.*;

class client{
    int PORT=4444;
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
					if(ip.indexOf('.') == -1)
					    continue;
					String subnet = getSubnet(ip);
					for (int i=1;i<255;i++){
						String host=subnet + "." + i;
						try {
							cs = new Socket(host, PORT);
							String id = handleSoc(cs);
							if ( id != null) {
								util.newClient(ip, id);
								ipaddress.add(ip);
							}
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
	public ArrayList<String> findFromDB(){
		ArrayList<String> add = new ArrayList<String>();
		ArrayList<String> ip = new ArrayList<String>();
		add = util.getClient();
		Socket cs = null;
		for (String host : add){
			try {
				cs = new Socket(host, PORT);
				if (handleSoc(cs) != null)
					ip.add(host);
			} catch (Exception se){
			} finally {
				if ( cs != null)
					try {cs.close();} catch(Exception e){}
			}
		} 
		return ip;
	}
	private String handleSoc(Socket cs){
		String id=null;
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			BufferedOutputStream out = new BufferedOutputStream(cs.getOutputStream());
			System.out.println("ip--> " + cs.getInetAddress().getHostAddress());
			id = in.readLine();
			System.out.println("unique id is:" + id);
			String cmd = "cmd";
			out.write(cmd.getBytes());
			out.flush();
		}catch(Exception e){
			return id;
		}
		return null;
	}
	private String getSubnet(String ip){
		String subnet;
		String[] a = ip.split("\\.");
		subnet = a[0] + "." + a[1] + "." + a[2];
		return subnet;
	}
	public static void main(String args[]){
		client c = new client();
		c.findAll();
	}
}
