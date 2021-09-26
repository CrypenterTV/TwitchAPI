package fr.crypenter.twitchapi.bot;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import fr.crypenter.twitchapi.config.ConfigSaver;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RefreshToken extends Thread {

    private TwitchBot twitchBot;
    private boolean running;

    public RefreshToken(TwitchBot twitchBot) {
        this.twitchBot = twitchBot;
    }

    @Override
    public void run() {

        running = true;

        try {


            while(true) {

                Thread.sleep(2000);

                if(!isValid()) {

                    refreshToken();

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            running = false;
        }

    }

    public boolean isRunning() {
        return running;
    }

    public void refreshToken() {

        try {

            String url = "https://id.twitch.tv/oauth2/token?grant_type=refresh_token&refresh_token=" + twitchBot.getRefreshToken() + "&client_id=" + getTwitchBot().getClientId() + "&client_secret=" + getTwitchBot().getClientSecret();
            URL obj = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();

            httpURLConnection.setRequestMethod("POST");

            int responseCode = httpURLConnection.getResponseCode();
            String responseMessage = httpURLConnection.getResponseMessage();

            if(responseCode != 200) {
                System.out.println("Error during refreshing the access token at " + url + ". Code: " + responseCode + " Message: " + responseMessage);
                return;
            }

            JsonParser jsonParser = new JsonParser();
            JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) httpURLConnection.getContent()));

            String access_token = root.getAsJsonObject().get("access_token").getAsString();
            String refresh_token = root.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("NEW ACCESS TOKEN:  " + access_token);
            System.out.println("NEW REFRESH TOKEN:  " + refresh_token);

            twitchBot.setRefreshToken(refresh_token);
            twitchBot.setAccessToken(access_token);

            ConfigSaver.saveConfig(twitchBot);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isValid() {

        try {

            String url = "https://api.twitch.tv/helix/subscriptions?broadcaster_id=" + twitchBot.getUserId();
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + twitchBot.getAccessToken());
            connection.setRequestProperty("Client-Id", twitchBot.getClientId());

            int responseCode = connection.getResponseCode();

            if(responseCode == 401) {
                System.out.println("The access token is expired.");
                return false;
            }
            else if(responseCode == 200) {
                ConfigSaver.saveConfig(twitchBot);
                return true;
            }
            else {
                System.out.println("[ERROR] Your credentials are not correct.");
            }




        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }


    public TwitchBot getTwitchBot() {
        return twitchBot;
    }

    public void setTwitchBot(TwitchBot twitchBot) {
        this.twitchBot = twitchBot;
    }
}

