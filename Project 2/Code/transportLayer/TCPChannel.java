package transportLayer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPChannel extends Channel {
    public int remoteNodeID;
    public Socket socket;

    public TCPChannel(int channelID) {
        super(channelID);
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void send(String msg) {
        PrintWriter outToServer = null;
        try {
            outToServer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        outToServer.println(msg);
    }

    @Override
    public String receive() {
        return null;
    }

    @Override
    public void close() {

    }
}
