package transportLayer;

public abstract class Channel {
    public int channelID;

    public Channel(int channelID) {
        this.channelID = channelID;
    }

    public abstract void send(String message);

    public abstract String receive();

    public abstract void close();
}
