import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Connection {
    private String host;
    private RequestType request;
    private Date date;
    private String address;
    private int port;

    public Connection(String host, RequestType request, String address, int port) {
        this.host = extractURL(host);
        this.request = request;
        this.date = new Date();
        this.address = address;
        this.port = port;
    }

    public Connection() {
        this.host = "";
        this.request = RequestType.NONE;
        this.address = "";
        this.port = -1;
        this.date = new Date();
    }

    private String extractURL(String str) {
        String regex
            = "\\b((www?)."
              + "[-a-zA-Z0-9+&@#/%?="
              + "~_|!:, .;]*[-a-zA-Z0-9+"
              + "&@#%=~_|])";

        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
  
        Matcher m = p.matcher(str);
        while (m.find()) {
            return (str.substring(m.start(0), m.end(0)));
        }
        return "";
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
        return new SimpleDateFormat("MMM dd YYYY HH:mm:ss", Locale.US).format(date) + " " + address + " " + host;
    }

    @Override
    public String toString() {
        return "Host: " + host + "\nRequest: " + request + "\nAddress: " + address + "\nPort: " + port;
    }
}
