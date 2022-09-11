import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class RequestHandler extends Thread {	
	private Socket clientSocket;
	private InputStream inFromClient;
	private OutputStream outToClient;
	private ProxyServer server;
	private Connection connection;

	public RequestHandler(Socket clientSocket, ProxyServer proxyServer) {
		this.clientSocket = clientSocket;
		this.server = proxyServer;
		connection = new Connection();
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
			int num_bytes = inFromClient.read(request_bytes);
			String request_string = new String(request_bytes, 0, num_bytes);
			if (request_string.contains("Host:") && request_string.contains("GET")) {
				connection = new Connection(request_string,RequestType.GET,clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
			}
			if (connection.isSupported()) {
				server.writeLog(connection.getLogEntry());
				if (server.getCache(connection.getFullHostAddress()) == null) {
					proxyServertoClient(request_bytes);
				} else {
					sendCachedInfoToClient(server.getCache(connection.getFullHostAddress()));
				}
			}
			// Close our connection
			in.close();
			clientSocket.close();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	private void proxyServertoClient(byte[] clientRequest) {
		try {
			System.out.println("Accessing from server:");
			Socket toWebServerSocket = new Socket(connection.getHostAddress(),connection.getHostPort());
			InputStream inFromServer = toWebServerSocket.getInputStream();
			OutputStream outToServer = toWebServerSocket.getOutputStream();
			// Create Buffered output stream to write to cached copy of file
			String fileName = "cached/" + generateRandomFileName() + ".dat";
			File file = new File(fileName);
			FileOutputStream fileWriter = new FileOutputStream(file);
			// to handle binary content, byte is used
			byte[] serverReply = new byte[4096];
			outToServer.write(clientRequest);
			outToServer.flush();
			int in;
			while((in = inFromServer.read(serverReply)) != -1) {
				outToClient.write(serverReply, 0, in);
				outToClient.flush();
				fileWriter.write(serverReply, 0, in);
				fileWriter.flush();
			}
			server.putCache(connection.getFullHostAddress(),fileName);
			fileWriter.close();
			inFromServer.close();
			outToServer.close();
			toWebServerSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}

	// Sends the cached content stored in the cache file to the client
	private void sendCachedInfoToClient(String fileName) {
		System.out.println("Accessing from cache:");
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(fileName));
			outToClient.write(bytes);
			outToClient.flush();
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