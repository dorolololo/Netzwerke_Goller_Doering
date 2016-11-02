
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 
 * Loesung von Aufgabe 2.2: Leetspeak Erweiterung für Webinhalte
 * @author D.Doering, dorina.doering@yahoo.de
 * @author D.Goller, goller@hm.edu
 * @version 31.10.2016
 */
public class LeetServ {
	
	private final static int LOCAL_PORT = 8082;
	
	private final static String CONFIG_LOC = "LeetServ.config";
	
	private final static int TARGET_HOST_PORT = 80;
	
	private final static String FIELD_END = "\r\n";
	
	private final static String LENGTH_FIELD = "Content-Length: ";
	
	private final static String ACCEPT_ENCODE_FIELD = "Accept-Encoding: ";
	
	private final static String CONTENT_TYPE_FIELD = "Content-Type: ";
	
	private final static String TEXT_CONTENT_TYPE = "text/html";
	
	private final static String HOST_FIELD = "Host: ";
	
	private final static String[] FILTER_WORDS = {
			"<img src=.+>", "<img src=\"http://fi.cs.hm.edu/fi/hm-logo.png\">",
			"MMIX", "|V||V|! }{",
			"Java", " _|4 |/4",
			"Computer", "(0|V|9|_|732",
			"RISC", "2!5[",
			"CISC", "(!5(",
			"Debugger", "|)38|_|6632",
			"Informatik", "!11PH02|V|7!X",
			"Student", "57|_||)3/\\/7",
			"Studentin", "57|_||)3117!/\\/",
			"Studierende", "57|_||)!32311|)3",
			"Windows", "uu!11|)0uu5",
			"Linux", "1!11|_|x"
	};

	private final BufferedReader inClient;
	private final BufferedWriter outClient;
	private final BufferedReader inHost;
	private final BufferedWriter outHost;
	private final String targetHost;
	
	public static void main(String[] args) throws IOException {
		final String targetHost;
		if (args.length > 0) {
			targetHost = args[0];
		} else {
			try (FileReader configFile = new FileReader(CONFIG_LOC);
			BufferedReader bufferedReader = new BufferedReader(configFile)) {
				targetHost = bufferedReader.readLine();
			}
		}
		System.out.println("Target Host @" + targetHost);
		try(ServerSocket server = new ServerSocket(LOCAL_PORT)) {
			System.out.println("Server @" + LOCAL_PORT);
			while (true) {
				try (Socket client = server.accept();
						Socket host = new Socket(targetHost, TARGET_HOST_PORT)) {
					LeetServ connection = new LeetServ(client, host, targetHost);
					connection.run();
				}			
			}
		}

	}
	
	public LeetServ(Socket client, Socket host, String targetHost) throws IOException {
		this.targetHost = targetHost;				
		inClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
		outClient = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		inHost = new BufferedReader(new InputStreamReader(host.getInputStream()));
		outHost = new BufferedWriter(new OutputStreamWriter(host.getOutputStream()));
	}

	public void run() throws IOException {
		try {
			while (true) {
				String clientHeader = getClientHeader();
				clientHeader = convertClientHeader(clientHeader);
				sendHost(clientHeader);
				String hostHeader = getHostHeader();
				sendClient(hostHeader);
				int bodyLength = getBodyLength(hostHeader);
				if (bodyLength > 0) {
					String hostBody = getHostBody(bodyLength);
					if (getBodyType(hostHeader).startsWith(TEXT_CONTENT_TYPE)) {
						hostBody = convertBody(hostBody);
					}
					sendClient(hostBody);		
				}											
			}
		} catch (IOException e) {
			//Exit loop
		}
	}

	private String getClientHeader() throws IOException {
		return getHeader(inClient);
	}
	
	private String getHostHeader() throws IOException {
		 return getHeader(inHost);
	}
	
	private String getHeader(BufferedReader in) throws IOException {
		final StringBuilder result = new StringBuilder();
		String currentLine;
		do {
			if ((currentLine = in.readLine()) == null) {
				throw new IOException(); // EOF
			}
			result.append(currentLine + FIELD_END);		
		} while (!currentLine.isEmpty()); // End of header? 
		return result.toString();
	}
	
	private String getHostBody(int bodyLength) throws IOException {
		return getBody(inHost, bodyLength);
	}
	
	private int getBodyLength(String header) {
		final String result = getHeaderField(header, LENGTH_FIELD);
		return result == null ? -1 : Integer.parseInt(result);
	}
	
	private String getBodyType(String header) {
		return getHeaderField(header, CONTENT_TYPE_FIELD);
	}
	
	private String getHeaderField(String header, String field) {
		String[] tmp = header.split(FIELD_END);
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].startsWith(field)) {
				return tmp[i].substring(field.length());
			}
		}
		return null;
	}
	
	private String getBody(BufferedReader in, int bodyLength) throws IOException {
		if (bodyLength < 0) {
			throw new IllegalArgumentException();
		}
		final char[] bodyBuffer = new char[bodyLength];
		int bytesRead = 0;
		while (bytesRead < bodyLength) {
			if ((bytesRead += inHost.read(bodyBuffer, bytesRead, bodyLength - bytesRead)) < 0) {
				throw new IOException();  // EOF
			}	
		}							
		return new String(bodyBuffer);
	}
	
	private void sendHost(String str) throws IOException {
		outHost.write(str);
		outHost.flush();
	}
	
	private void sendClient(String str) throws IOException {
		outClient.write(str);
		outClient.flush();
	}
	
	private String convertBody(String body) {
		String result = body;
		for (int i = 0; i < FILTER_WORDS.length; i+=2) {
			result = result.replaceAll(FILTER_WORDS[i], FILTER_WORDS[i+1]);
		}
		return result;
	}
	
	private String convertClientHeader(String header) {
		List<String> headerLines = new ArrayList<>(Arrays.asList(header.split(FIELD_END)));
		headerLines.removeIf(line -> line.startsWith(ACCEPT_ENCODE_FIELD));
		headerLines.replaceAll(line -> line.startsWith(HOST_FIELD) ? HOST_FIELD + targetHost : line);
		headerLines.add(FIELD_END);
		return String.join(FIELD_END, headerLines);
	}
	
	
}
