package gestor.client;

import java.net.*;
import java.io.*;
import java.util.*;

class clients{
	public static void main(String args[]) {
		Socket cs ;		
		Process proc;
		//string array
		String[] command = new String[50];
		
		command[0] = "shutdown -s -t";
		command[1] = "shutdown -r";
		command[2] = "shutdown -l";
		command[3] = "shutdown -h";
		command[4] = "netstat -n";
		command[5] = "NetSh Advfirewall set allprofiles state off";
		command[6] = "NetSh Advfirewall set allprofiles state on";
		command[7] = "Netsh Advfirewall show allprofiles";
		command[8] = "wmic diskdrive get status";
		command[9] = "sfc /SCANNOW";
		command[10] = "net user (user_name) (password) /add";
		command[11] = "net user (user_name) /del";
		command[12] = "net user (user_name) *";
		command[13] = "bootcfg /addsw";
		command[14] = "chdir";
		command[15] = "chdsk c: /p";
		command[16] = "chkntfs /d";
		command[17] = "cipher /e";
		command[18] = "cipher /d";
		command[19] = "compact /c /s:c: (file_name)";
		command[20] = "compact /u /s:c: (file_name)";
		command[21] = "compact /a";
		command[22] = "convert [volume] /fs:ntfs [/v] [/cvtarea:FileName] [/nosecurity] [/x]";
		command[23] = "copy /d";
		command[24] = "xcopy source [destination]";
//		command[25] = "driverquery  [/s Computer] [/u Domain\User /p Password] [/fo {TABLE|LIST|CSV}] [/nh] [/v] [/si]";
		command[26] = "ftp -d";
		command[27] = "ipconfig /all";
		command[28] = "ipseccmd -u";
		command[29] = "label [Drive:][label]";
		command[30] = "label [/MP][volume][label]";
		command[31] = "lpr -s";
		command[32] = " mkdir [Drive:]Path";
		command[33] = "mmc Path\filename.msc [/a] [/64] [/32]";
		command[34] = " mountvol [Drive:]Path VolumeName";
		command[35] = "move [{/y|/-y}] [Source] [target]";
		command[36] = "rmdir [Drive:]Path [/s] [/q]";
		command[37] = "secedit /analyze /db FileName [/cfg FileName] [/log FileName] [/quiet]";
		command[38] = "";
		command[39] = "";
		command[40] = "";
		command[41] = "";
		command[42] = "";		


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
