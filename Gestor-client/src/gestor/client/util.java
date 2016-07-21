package gestor;

import java.io.*;
import java.util.Date;

class util {
	void fun() {
		System.out.println("sad");
	}
	static void log(String type, String message) {
		String timeStamp = new java.text.SimpleDateFormat("h:mm a dd/MM/yy").format(new Date());
		String logText = "["+timeStamp+"]  " + type + "\t" + message;
		try {
			File file = new File("gestor/log/log.txt");
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(logText + "\n");
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}