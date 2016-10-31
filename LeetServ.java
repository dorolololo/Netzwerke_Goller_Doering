
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class LeetServ {

	//private final static String GET_FIELD = "GET ";

	private final static String FIELD_END = "\r\n";

	//private final static String HEADER_END = "\r\n\r\n";

	private final static String HOST_FIELD = "Host: ";

	private final static String LENGTH_FIELD = "Content-Length: ";
	
	private final static String ACCEPT_ENCODE_FIELD = "Accept-Encoding: ";
	
	//private final static String MODIFIED_SINCE_FIELD = "If-Modified-Since: ";

	private final static String TARGET_HOST = "mmix.cs.hm.edu";
	
	private final static int LOCAL_PORT = 8082;

	public static void main(String[] args) throws IOException {
		final String th = args.length > 0 ? args[0] : TARGET_HOST;
		try(ServerSocket serv = new ServerSocket(LOCAL_PORT)) {
			System.out.println("#started ServerSocket @ " + LOCAL_PORT);
			while (true) {
				try(final Socket client = serv.accept();					
						final Socket host = new Socket(th, 80)) {
					System.out.println("#Client and Host(" + th + ") connected");
					try {
						final BufferedReader inClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
						final BufferedWriter outClient = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
						final BufferedReader inHost = new BufferedReader(new InputStreamReader(host.getInputStream()));
						final BufferedWriter outHost = new BufferedWriter(new OutputStreamWriter(host.getOutputStream()));
						while (true) {
							String currentHeaderLine = null;
							while (!"".equals(currentHeaderLine)) {
								currentHeaderLine = inClient.readLine();
								if (currentHeaderLine == null) {
									throw new IOException();
								}
								System.out.println(currentHeaderLine);
								if (currentHeaderLine.startsWith(HOST_FIELD)) {
									outHost.write(HOST_FIELD + TARGET_HOST + FIELD_END);
								} else {
									if (!(currentHeaderLine.startsWith(ACCEPT_ENCODE_FIELD))) {
										outHost.write(currentHeaderLine + FIELD_END);
									}								
								}
							}
							outHost.flush();
							System.out.println("#Header received from Client and send to Host");
							currentHeaderLine = null;
							int bodyLength = 0;
							while (!"".equals(currentHeaderLine)) {
								currentHeaderLine = inHost.readLine();
								if (currentHeaderLine == null) {
									throw new IOException();
								}
								System.out.println(currentHeaderLine);
								if (currentHeaderLine.startsWith(LENGTH_FIELD)) {
									bodyLength = Integer.parseInt(currentHeaderLine.substring(LENGTH_FIELD.length()));
								}
								outClient.write(currentHeaderLine + FIELD_END);		
								
							}
							outClient.flush();
							System.out.println("#Header received from Host and send to Client");
							if (bodyLength > 0) {
								final char[] bodyBuffer = new char[bodyLength];
								int bytesRead = 0;
								while (bytesRead < bodyLength) {
									if ((bytesRead += inHost.read(bodyBuffer, bytesRead, bodyLength - bytesRead)) < 0) {
										throw new IOException();
									}	
								}							
								final String html = new String(bodyBuffer);
								System.out.println(html);
								final String leetHtml = htmlLeetConvert(html);
								outClient.write(leetHtml);
								outClient.flush();
								System.out.println("#Body received from Host and send to Client");
							}

						}
					} catch (IOException e) {
						System.out.println("#IOException");
					}
					
				}
			}
		}

	}

	static String htmlLeetConvert(String html) {
		final StringBuilder stringBuilder = new StringBuilder(html);
		final String[] searchWords = {
				"<img src=\"", "http://fi.cs.hm.edu/fi/hm-logo.png",
				"MMIX", "|V||V|! }{",
				"Java", " _|4 |/4",
				"Computer", "(0|V|9|_|732",
				"RISC", "2!$[",
				"CISC", "(!$(",
				"Debugger", "|)38|_|6632",
				"Informatik", "!11PH02|V|7!X",
				"Student", "$7|_||)3/\\/7",
				"Studentin", "$7|_||)3117!/\\/",
				"Studierende", "$7|_||)!32311|)3",
				"Windows", "uu!11|)0uu$",
				"Linux", "1!11|_|x"
		};
		for (int i = 0; i < searchWords.length; i+=2) {
			int lastIndex = 0;
			while((lastIndex = stringBuilder.indexOf(searchWords[i], lastIndex)) != -1) {
				int endIndex;
				if (i == 0) {
					lastIndex += searchWords[i].length();
					endIndex = stringBuilder.indexOf("\"", lastIndex);
					if (endIndex == -1) {
						throw new AssertionError();
					} 					
				} else {
					endIndex = lastIndex + searchWords[i].length();
				}
				stringBuilder.replace(lastIndex, endIndex, searchWords[i+1]);
			}		
		}		
		return stringBuilder.toString();
	}
}
