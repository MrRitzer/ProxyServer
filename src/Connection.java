import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Connection {
    private String host;
    private RequestType request;
    private Date date;
    private InetAddress address;

    public Connection(String host, RequestType request, InetAddress address) {
        this.host = host;
        this.request = request;
        this.date = new Date();
        this.address = address;
    }

    public String getHost() {
        return host;
    }

    public RequestType getRequest() {
        return request;
    }

    public Boolean isValid() {
        return host != "" && request != RequestType.NONE;
    }

    public String getLogEntry() {
        return new SimpleDateFormat("MMM dd YYYY HH:mm:ss", Locale.US).format(this.date) + " " + this.address.getHostAddress() + " " + this.host;
    }

    @Override
    public String toString() {
        return "Host: " + host + "\nRequest: " + request + "\nAddress: " + this.address.getHostAddress();
    }
}
