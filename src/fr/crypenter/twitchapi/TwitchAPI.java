package fr.crypenter.twitchapi;

import fr.crypenter.twitchapi.bot.TwitchBot;

public class TwitchAPI {

    private static int port = 6667;
    private static String urlString = "irc.chat.twitch.tv";

    private TwitchBot twitchBot;

    public TwitchAPI(TwitchBot twitchBot) {
        this.twitchBot = twitchBot;
    }

    public void writeServer(String request) {
        if(twitchBot.getOutput() == null) {
            System.out.println("ERROR: Failed to send a request because the output is null");
            return;
        }
        twitchBot.getOutput().println(request);
        System.out.println(">> " + request);
    }




    public static int getPort() {
        return port;
    }

    public static String getUrlString() {
        return urlString;
    }


    public TwitchBot getTwitchBot() {
        return twitchBot;
    }


}
