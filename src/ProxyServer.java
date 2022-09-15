import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyServer {
	// cache is a Map: the key is the URL and the value is the file name of the file
	// that stores the cached content
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
				new RequestHandler(proxySocket.accept(), this);
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

		String log = info;
        BufferedWriter bwriter = null;

        //write the log variable using the bufferedWriter to ProxyLog.txt
        try {
            bwriter = new BufferedWriter(new FileWriter(logFileName, true));
            bwriter.write("\n"+ log);
        } 
        catch (IOException io) {
			io.printStackTrace();
        } //close the file
        finally {
            try {
                if (bwriter != null) {
                    bwriter.close();
                }
            } 
            catch (IOException io) {
				io.printStackTrace();
			}
		}
	}
}