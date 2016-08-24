import java.net.*;
import java.io.*;
import java.util.*;
class clientsnew
{
  public static void main(String args[])
    {
      Socket soc ;
      PrintStream ps;
      BufferedReader dis;
      BufferedInputStream procStdout;
      String value;
      Process proc;
	String cmd="";
      try
         {
           soc = new Socket("192.168.56.1",4444);
          dis= new BufferedReader(new InputStreamReader(soc.getInputStream()));
          value = dis.readLine();
               String parts[] = value.split("::");
		String par0 = parts[0];
		String par1 = parts[1];
		String par2 = parts[2];
		String par3 = parts[3];
		String par4 = parts[4];
		String values[]={"0","1","2","3","4","5","6","7","8","9"};
		String v0 = values[0];
		String v1 = values[1];
		String v2 = values[2];
		String val;
		if(par0.equals(v0)){
			val="shutdown";
			System.out.println(val);
		}
		else if(par0.equals(v1)){
			val="proxy";
			System.out.println(val);
		}
		else if(par0.equals(v2)){
			val="firewall";
			cmd=val+" "+par4;
			System.out.format(cmd);
		}
		else{
			System.out.println("not found");
		}
		System.out.println(cmd);
		//proc = Runtime.getRuntime().exec(new String(cmd));
              // procStdout = new BufferedInputStream(proc.getInputStream());
               
              //procStdout.close();
		
        }
        catch(IOException e)
        {
           System.out.println("the exception is:"+e);

        }

}    
}
