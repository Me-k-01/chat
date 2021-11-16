public class Client {
    int port;

    public Client(int port) {
        this.port = port;
        start();
    }
    public void start()  {
        
    }

    public static void main(String[] args) {
        Client client = new Client(3000); 
    }
}
