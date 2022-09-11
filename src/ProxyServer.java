import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.print.attribute.standard.MediaSize.ISO;


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
		
		Thread clearCache = new Thread(() -> {
			// list all the files in an array
			File[] files = cacheDir.listFiles();
			// delete each file from the directory
			for(File file : files) {
			  file.delete();
			}
			cacheDir.delete();
			System.out.println("Cleared cache:");
		});

		Runtime.getRuntime().addShutdownHook(clearCache);

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
		System.out.println(info);
        Logger logger = Logger.getLogger("ProxyLog");
        FileHandler proxyFileHandler;
        try {
            proxyFileHandler = new FileHandler(logFileName);
            logger.addHandler(proxyFileHandler);
			logger.info(info);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}