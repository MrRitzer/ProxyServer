import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;

// RequestHandler is thread that process requests of one client connection
public class RequestHandler extends Thread {	
	Socket clientSocket;
	InputStream inFromClient;
	OutputStream outToClient;
	private ProxyServer server;

	public RequestHandler(Socket clientSocket, ProxyServer proxyServer) {
		this.clientSocket = clientSocket;
		this.server = proxyServer;
		try {
			this.inFromClient = clientSocket.getInputStream();
			this.outToClient = clientSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.start();
	}
	
	@Override
	public void run() {
		try {
			byte[] request_bytes = new byte[1024];
			BufferedReader in = new BufferedReader(new InputStreamReader(inFromClient));
			String host = "";
			RequestType requestType = RequestType.NONE;
          
			int num_bytes = inFromClient.read(request_bytes);
			String request_string = new String(request_bytes, 0, num_bytes);
			if (request_string.contains("Host:")) {
				String temp[];
				temp = request_string.split(" ");
				host = temp[1];
			}
			if (request_string.contains("GET")) {
				requestType = RequestType.GET;
			}
			Connection connection = new Connection(host, requestType, clientSocket.getInetAddress());
			if (connection.isValid()) {
				System.out.println(connection);
				this.server.writeLog(connection.getLogEntry());
			}
			// Close our connection
			in.close();
			clientSocket.close();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		/**
			 * To do
			 * Process the requests from a client. In particular, 
			 * (1) Check the request type, only process GET request and ignore others
                         * (2) Write log.
			 * (3) If the url of GET request has been cached, respond with cached content
			 * (4) Otherwise, call method proxyServertoClient to process the GET request
			 *
		*/

	}

	
	private void proxyServertoClient(byte[] clientRequest) {
		FileOutputStream fileWriter = null;
		Socket toWebServerSocket = null;
		InputStream inFromServer;
		OutputStream outToServer;
		
		// Create Buffered output stream to write to cached copy of file
		String fileName = "cached/" + generateRandomFileName() + ".dat";
		
		// to handle binary content, byte is used
		byte[] serverReply = new byte[4096];
		
			
		/**
		 * To do
		 * (1) Create a socket to connect to the web server (default port 80)
		 * (2) Send client's request (clientRequest) to the web server, you may want to use flush() after writing.
		 * (3) Use a while loop to read all responses from web server and send back to client
		 * (4) Write the web server's response to a cache file, put the request URL and cache file name to the cache Map
		 * (5) close file, and sockets.
		*/
		
	}
	
	
	
	// Sends the cached content stored in the cache file to the client
	private void sendCachedInfoToClient(String fileName) {
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(fileName));
			outToClient.write(bytes);
			outToClient.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (clientSocket != null) {
				clientSocket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// Generates a random file name  
	public String generateRandomFileName() {
		String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
		SecureRandom RANDOM = new SecureRandom();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; ++i) {
			sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		return sb.toString();
	}
}