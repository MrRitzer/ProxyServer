import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Connection {
    private String host;
    private RequestType request;
    private Date date;
    private String address;
    private int port;

    public Connection(String host, RequestType request, String address, int port) {
        this.host = host;
        this.request = request;
        this.date = new Date();
        this.address = address;
        this.port = port;
    }

    public Connection() { }

    public String getHost() {
        return host;
    }

    public RequestType getRequest() {
        return request;
    }

    public Boolean isValid() {
        try {
            return host != "" && request != RequestType.NONE;
        } catch (Exception e) {
            return false;
        }
    }

    public String getLogEntry() {
        return new SimpleDateFormat("MMM dd YYYY HH:mm:ss", Locale.US).format(date) + " " + address + " " + host;
    }

    @Override
    public String toString() {
        return "Host: " + host + "\nRequest: " + request + "\nAddress: " + address + "\nPort: " + port;
    }
}
