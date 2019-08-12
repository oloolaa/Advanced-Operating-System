package transportLayer;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import utilization.MessageReceiveService;

public class TCPClientHandler implements Runnable {
    int destChannelID;
    private Socket socket;

    public TCPClientHandler(Socket socket, int channelID) {
        this.socket = socket;
        this.destChannelID = channelID;
    }

    @Override
    public void run() {
        Scanner reader = null;
        try {
            reader = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            String receivedMsg;
            while (reader.hasNextLine()) {
                receivedMsg = reader.nextLine();
                MessageReceiveService.getInstance().receive(receivedMsg, destChannelID);
            }
        }
    }
}
