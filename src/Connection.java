import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Connection {
    private String hostAddress;
    private String fullHostAddress;
    private int hostPort;
    private RequestType requestType;
    private Date date;
    private String clientAddress;
    private int clientPort;

    public Connection(String host, RequestType request, String address, int clientPort) {
        this.hostAddress = extractHost(host);
        this.fullHostAddress = extractFullHost(host);
        this.hostPort = extractHostPort(host);
        this.requestType = request;
        this.date = new Date();
        this.clientAddress = address;
        this.clientPort = clientPort;
    }

    public Connection() {
        this.hostAddress = "";
        this.fullHostAddress = "";
        this.hostPort = -1;
        this.requestType = RequestType.NONE;
        this.clientAddress = "";
        this.clientPort = -1;
        this.date = new Date();
    }

    private String extractHost(String host) {
        String regex
            = "\\b((HOST:?)"
              + "[-a-zA-Z0-9+&@#/%?="
              + "~_|!:, .;]*[-a-zA-Z0-9+"
              + "&@#%=~_|])";

        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
  
        Matcher m = p.matcher(host);
        while (m.find()) {
            return (host.substring(m.start(0), m.end(0)).split(" ")[1]);
        }
        return "";
    }

    private String extractFullHost(String host) {
        String regex
            = "\\b((GET?)"
              + "[-a-zA-Z0-9+&@#/%?="
              + "~_|!:, .;]*[-a-zA-Z0-9+"
              + "&@#%=~_|])";

        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
  
        Matcher m = p.matcher(host);
        while (m.find()) {
            return (host.substring(m.start(0), m.end(0)).split(" ")[1]);
        }
        return "";
    }

    private int extractHostPort(String host) {
        if (host.contains("https") || host.contains("443")) {
            return 443;
        } else {
            return 80;
        }
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public String getFullHostAddress() {
        return fullHostAddress;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public int getHostPort() {
        return hostPort;
    }

    public Boolean isSupported() {
        return hostAddress != "" && requestType != RequestType.NONE;
    }

    public String getLogEntry() {
        return new SimpleDateFormat("MMM dd YYYY HH:mm:ss", Locale.US).format(date) + " " + clientAddress + " " + hostAddress;
    }

    @Override
    public String toString() {
        return "Host: " + hostAddress + "\nRequest: " + requestType + "\nAddress: " + clientAddress + "\nPort: " + clientPort;
    }
}
