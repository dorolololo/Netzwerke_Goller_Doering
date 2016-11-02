import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;

/** 
 * Loesung von Aufgabe 1.2: Ein erster Netzwerkdienst
 * @author D.Doering, dorina.doering@yahoo.de
 * @author D.Goller, goller@hm.edu
 * @version 31.10.2016
 */
public class XinetdServ {

	private final static String END_MSG = "Zugriff aufgezeichnet am: %s!%n";
	
	private final static String FIELD_END = "\r\n";
	
	private final static String SAFE_LOC = "XinetdServ.log";

	public static void main(String... args) throws IOException{	
		try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			FileWriter output = new FileWriter(SAFE_LOC)) {
			String currentLine;
			do {
				if ((currentLine = input.readLine()) == null) break; // EOF
				output.write(currentLine + FIELD_END);		
			} while (!currentLine.isEmpty()); // End of header? 
		}		
		final String currentDate = DateFormat.getInstance().format(new Date());
		System.out.printf(END_MSG, currentDate);
	}

}
