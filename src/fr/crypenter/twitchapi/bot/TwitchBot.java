package fr.crypenter.twitchapi.bot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import fr.crypenter.twitchapi.TwitchAPI;
import fr.crypenter.twitchapi.channel.TwitchChannel;
import fr.crypenter.twitchapi.config.ConfigManager;
import fr.crypenter.twitchapi.entities.User;
import fr.crypenter.twitchapi.events.EventFollow;
import fr.crypenter.twitchapi.events.EventMessageReceived;
import fr.crypenter.twitchapi.events.EventSubscriber;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
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

    private TwitchChannel twitchChannel;

    private List<EventMessageReceived> eventMessageReceivedList;
    private List<EventFollow> eventFollowList = new ArrayList<EventFollow>();
    private List<EventSubscriber> eventSubscriberList = new ArrayList<EventSubscriber>();

    private String clientId;
    private String userId;
    private String accessToken;
    private String clientSecret;

    private FollowListener followListener;
    private SubscriberListener subscriberListener;

    private String refreshToken;

    private RefreshToken refreshingToken;

    public TwitchBot(String username, String password, int timeout) {
        this.username = username;
        this.password = password;
        this.timeout = timeout;
        this.twitchAPI = new TwitchAPI(this);
        this.twitchListener = new TwitchListener(this);
        this.followListener = new FollowListener(this);
        this.subscriberListener = new SubscriberListener(this);
        this.eventMessageReceivedList = new ArrayList<EventMessageReceived>();
        ConfigManager.initConfig();
    }

    public void startRefreshingToken() {
        refreshingToken = new RefreshToken(this);
        refreshingToken.start();


    }


    public void connect(TwitchChannel channel) {
        if(accessToken == null || refreshToken == null || clientId == null || userId == null || clientSecret == null) {
                for(int i = 0; i < 5; i++) {
                    System.out.println("[WARN] YOU NEED TO PROVIDE THE ACCESS TOKEN, THE REFRESH TOKEN, THE CLIENTID, THE USERID AND THE CLIENTSECRET.");
                }
        }
        this.twitchChannel = channel;
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

    public void addFollowListener(EventFollow eventFollow) {
        eventFollowList.add(eventFollow);
        if(!followListener.isStarted()) {
            followListener.start();
        }
    }

    public void removeFollowListener(EventFollow eventFollow) {
        if(eventFollowList.contains(eventFollow)) {
            eventFollowList.remove(eventFollow);
        }
    }

    public void addSubscriberListener(EventSubscriber eventSubscriber) {
        eventSubscriberList.add(eventSubscriber);
        if(!subscriberListener.isStarted()) {
            subscriberListener.start();
        }
    }

    public void removeSubscriberListener(EventSubscriber eventSubscriber) {
        if(eventSubscriberList.contains(eventSubscriber)) {
            eventSubscriberList.remove(eventSubscriber);
        }
    }

    private void auth() {
        twitchAPI.writeServer("PASS " + password);
        twitchAPI.writeServer("NICK " + username);
    }




    public boolean isInStream() {

        try {

            if(clientId == null || accessToken == null || userId == null) {
                System.out.println("Your clientId, accessToken and userId is missing.");
                return false;
            }

            String url = "https://api.twitch.tv/helix/streams";
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-Id", clientId);
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            if(responseCode != 200) {
                System.out.println("Error during a request to " + url + " | Error " + responseCode + "  " + responseMessage);
                return false;
            }

            JsonParser jsonParser = new JsonParser();
            JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) connection.getContent()));
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if(response.length() > 1) {
                return true;
            }
            //System.out.println(root.getAsJsonObject().get("type").getAsString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<String> getLastFollowers() {

        try {

            if(clientId == null || accessToken == null || userId == null) {
                System.out.println("Your clientId, accessToken and userId is missing.");
                return null;
            }

            String url = "https://api.twitch.tv/helix/users/follows?to_id=" + userId;
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-Id", clientId);
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            if(responseCode != 200) {
                System.out.println("Error during a request to " + url + " | Error " + responseCode + "  " + responseMessage);
                return null;
            }

            JsonParser jsonParser = new JsonParser();
            JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) connection.getContent()));
            JsonArray followers = root.getAsJsonObject().get("data").getAsJsonArray();

            List<String> followersList = new ArrayList<String>();

            for(JsonElement follower : followers) {
                followersList.add(follower.getAsJsonObject().get("from_name").getAsString().replace("\"", ""));
            }

            return followersList;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    public int getFollowersNum() {

        if(clientId == null || accessToken == null || userId == null) {
            System.out.println("Your clientId, accessToken and userId is missing.");
            return 0;
        }

        try {

            String url = "https://api.twitch.tv/helix/users/follows?to_id=" + userId;
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-Id", clientId);
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            if(responseCode != 200) {
                System.out.println("Error during a request to " + url + " | Error " + responseCode + "  " + responseMessage);
                return 0;
            }

            JsonParser jsonParser = new JsonParser();
            JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) connection.getContent()));

            return Integer.parseInt(root.getAsJsonObject().get("total").getAsString());


        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getSubscribersNum() {

        try {

            String url = "https://api.twitch.tv/helix/subscriptions?broadcaster_id=" + userId;
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Client-Id", clientId);

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            if(responseCode != 200) {
                System.out.println("Error during a request to " + url + " | Error " + responseCode + "  " + responseMessage);
                return 0;
            }

            JsonParser jsonParser = new JsonParser();
            JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) connection.getContent()));

            return Integer.parseInt(root.getAsJsonObject().get("total").getAsString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<String> getLastSubscribers() {

        try {

            String url = "https://api.twitch.tv/helix/subscriptions?broadcaster_id=" + userId;
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Client-Id", clientId);

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            if(responseCode != 200) {
                System.out.println("Error during a request to " + url + " | Error " + responseCode + "  " + responseMessage);
                return null;
            }

            JsonParser jsonParser = new JsonParser();
            JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) connection.getContent()));
            JsonArray data = root.getAsJsonObject().get("data").getAsJsonArray();

            List<String> subscribers = new ArrayList<String>();

            for(JsonElement sub : data) {
                subscribers.add(sub.getAsJsonObject().get("user_name").getAsString());
            }

            return subscribers;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getModerators() {

        try {

            String url = "https://api.twitch.tv/helix/moderation/moderators?broadcaster_id=" + userId;
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Client-Id", clientId);

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            if(responseCode != 200) {
                System.out.println("Error during a request to " + url + " | Error " + responseCode + "  " + responseMessage);
                return null;
            }

            JsonParser jsonParser = new JsonParser();
            JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) connection.getContent()));
            JsonArray data = root.getAsJsonObject().get("data").getAsJsonArray();

            List<String> moderators = new ArrayList<String>();

            for(JsonElement mod : data) {
                moderators.add(mod.getAsJsonObject().get("user_name").getAsString());
            }

            return moderators;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public void ban(User user) {
        sendMessage("/ban " + user, twitchChannel);
    }

    public void ban(User user, String reason) {
        sendMessage("/ban " + user + " " + reason, twitchChannel);
    }

    public void unban(User user) {
        sendMessage("/unban " + user, twitchChannel);
    }

    public void timeout(User user, int minutes) {
        sendMessage("/timeout " + user + " " + (minutes*60), twitchChannel);
    }

    public void timeout(User user, int minutes, String reason) {
        sendMessage("/timeout " + user + " " + (minutes*60) + " " + reason, twitchChannel);
    }

    public void host(String channel) {
        sendMessage("/host " + channel, twitchChannel);
    }

    public void unHost() {
        sendMessage("/unhost", twitchChannel);
    }

    public void slowMode(int sec) {
        sendMessage("/slow " + sec, twitchChannel);
    }

    public void slowModeOff() {
        sendMessage("/slowoff", twitchChannel);
    }

    public void subOnly() {
        sendMessage("/subscribers", twitchChannel);
    }

    public void subOnlyOff() {
        sendMessage("/subscribersoff", twitchChannel);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
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

    public TwitchChannel getTwitchChannel() {
        return twitchChannel;
    }

    public TwitchListener getTwitchListener() {
        return twitchListener;
    }

    public List<EventFollow> getEventFollowList() {
        return eventFollowList;
    }

    public List<EventSubscriber> getEventSubscriberList() {
        return eventSubscriberList;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
