import java.net.*;
import java.io.*;
import java.util.*;
class serversnew
{
  public static void main(String args[])
    {
      ServerSocket ss =null;
      Socket cs ;
      PrintStream ps;
      BufferedReader dis;
      
      try
        {
            ss= new ServerSocket(4444);
            System.out.println("server ready...");
            while(true)
              {
               cs= ss.accept();
               ps=new PrintStream(cs.getOutputStream());
            DataOutputStream out = new DataOutputStream(cs.getOutputStream());
            
	       String value="2::5::shiva::55::192.168.1.2";
	       out.writeBytes(value);
               out.flush();
               ps.close();
               

              }
           
        }
        catch(IOException e)
        {
           System.out.println("the exception is:"+e);

        }

    }

}