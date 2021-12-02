public class SocketDisconnected extends RuntimeException {
    public SocketDisconnected()
    {
        super("Le socket est déconnecté");
    }
}
