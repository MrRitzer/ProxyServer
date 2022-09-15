public class App {
    public static void main(String[] args) throws Exception {
        new ProxyServer().startServer(Integer.parseInt(args[0]));
    }
}
