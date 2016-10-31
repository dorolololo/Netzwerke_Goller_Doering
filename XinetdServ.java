import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;

public class XinetdServ {

	final static String END_MSG = "Zugriff aufgezeichnet am: ";
	final static String SAFE_LOC = System.getProperty("user.home") + File.separator + "XinetdServ.log";
	final static int TIMEOUT_MILLIS = 3000;

	public static void main(String[] args) throws IOException, InterruptedException {
		
		try (InputStream input = System.in;
			FileOutputStream output = new FileOutputStream(SAFE_LOC)) {
			byte [] buffer = new byte[256];
			int bytesRead = 0;
			boolean timeout = false;
			while(!timeout && (bytesRead = input.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
				final long currentMillis = System.currentTimeMillis();
				while (input.available() <= 0 && !timeout) {
					Thread.sleep(50);
					if (System.currentTimeMillis() - currentMillis > TIMEOUT_MILLIS) {
						timeout = true;
					}
				}
			}
		}		
		System.out.println(END_MSG + DateFormat.getInstance().format(new Date()) + "!");
	}

}
