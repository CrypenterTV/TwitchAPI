package fr.crypenter.twitchapi.bot;

import fr.crypenter.twitchapi.TwitchAPI;
import fr.crypenter.twitchapi.channel.TwitchChannel;
import fr.crypenter.twitchapi.events.EventMessageReceived;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TwitchBot {

    private String username;
    private String password;

    private int timeout;

    private Socket socket;

    private PrintWriter output;
    private DataInputStream input;

    private TwitchAPI twitchAPI;

    private TwitchListener twitchListener;

    private volatile boolean connected = false;

    private List<EventMessageReceived> eventMessageReceivedList;


    public TwitchBot(String username, String password, int timeout) {
        this.username = username;
        this.password = password;
        this.timeout = timeout;
        this.twitchAPI = new TwitchAPI(this);
        this.twitchListener = new TwitchListener(this);
        this.eventMessageReceivedList = new ArrayList<EventMessageReceived>();
    }


    public void connect(TwitchChannel channel) {
        try {

            new Thread(() -> {
                try {
                    socket = new Socket(InetAddress.getByName(TwitchAPI.getUrlString()), TwitchAPI.getPort());
                    input = new DataInputStream(socket.getInputStream());
                    output = new PrintWriter(socket.getOutputStream(), true);
                    twitchListener.start();
                    auth();
                    twitchAPI.writeServer("JOIN " + channel.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {

            connected = false;

            if(output != null) {
                output.close();
            }

            if(input != null) {
                input.close();
            }

            if(socket != null && !socket.isClosed()) {
                socket.close();
            }

            System.out.println("Bot successfully disconnected.");

        } catch (IOException e) {
            connected = false;
            System.out.println("Bot successfully disconnected.");
        }
    }

    public void sendMessage(String message, TwitchChannel channel) {
        twitchAPI.writeServer("PRIVMSG " + channel.getName() + " " + message);
    }

    public void clearChannel(TwitchChannel channel) {
        twitchAPI.writeServer("PRIVMSG " + channel.getName() + " /clear");
    }


    public void addMessageListener(EventMessageReceived eventMessageReceived) {
        eventMessageReceivedList.add(eventMessageReceived);
    }

    public void removeMessageListener(EventMessageReceived eventMessageReceived) {
        if(eventMessageReceivedList.contains(eventMessageReceived)) {
            eventMessageReceivedList.remove(eventMessageReceived);
        }
    }

    private void auth() {
        twitchAPI.writeServer("PASS " + password);
        twitchAPI.writeServer("NICK " + username);
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getOutput() {
        return output;
    }

    public DataInputStream getInput() {
        return input;
    }

    public TwitchAPI getTwitchAPI() {
        return twitchAPI;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public List<EventMessageReceived> getEventMessageReceivedList() {
        return eventMessageReceivedList;
    }
}
