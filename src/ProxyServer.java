import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;


public class ProxyServer {
	//cache is a Map: the key is the URL and the value is the file name of the file that stores the cached content
	Map<String, String> cache;
	
	ServerSocket proxySocket;

	Boolean running;

	String logFileName = "log.txt";

	void startServer(int proxyPort) {
		cache = new ConcurrentHashMap<>();
		// create the directory to store cached files. 
		File cacheDir = new File("cached");
		if (!cacheDir.exists() || (cacheDir.exists() && !cacheDir.isDirectory())) {
			cacheDir.mkdirs();
		}

		try {
			proxySocket = new ServerSocket(proxyPort);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Listening for connection: ");
		while (true) {
			try {
                new RequestHandler( proxySocket.accept(), this );
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
	}

	public String getCache(String hashcode) {
		return cache.get(hashcode);
	}

	public void putCache(String hashcode, String fileName) {
		cache.put(hashcode, fileName);
	}

	public synchronized void writeLog(String info) {



		
			/**
			 * To do
			 * write string (info) to the log file, and add the current time stamp 
			 * e.g. String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			 *
			*/
	}

}