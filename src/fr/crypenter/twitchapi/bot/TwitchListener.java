package fr.crypenter.twitchapi.bot;

import fr.crypenter.twitchapi.entities.User;
import fr.crypenter.twitchapi.events.EventMessageReceived;

import java.io.DataInputStream;

public class TwitchListener extends Thread {

    private TwitchBot twitchBot;


    public TwitchListener(TwitchBot twitchBot) {
        this.twitchBot = twitchBot;
    }


    @Override
    public void run() {

        try {
            while(true) {

                if(twitchBot.getInput() == null) {
                    return;
                }

                String response = readData(twitchBot.getInput());
                if(response.length() < 1) {
                    continue;
                }

                System.out.println("<< " + response);

                if(response.contains("End of /NAMES list")) {
                    System.out.println("Bot is ready !");
                    twitchBot.setConnected(true);
                }
                if(response.startsWith("PING")) {
                    String pingContents = response.split(" ", 2)[1];
                    twitchBot.getTwitchAPI().writeServer("PONG " + pingContents);
                }
                if(response.contains("PRIVMSG")) {
                    for(EventMessageReceived eventMessageReceived : twitchBot.getEventMessageReceivedList()) {
                        String author = response.split("!")[0].split("@")[0].replace(":", "");
                        String message = response.split("PRIVMSG")[1].split(":", 0)[1];
                        eventMessageReceived.onMessageReceived(twitchBot, twitchBot.getTwitchChannel(), message, new User(author));
                    }
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String readData(DataInputStream input) {
        try {

            StringBuilder stringBuilder = new StringBuilder();
            byte[] bytes = new byte[input.available()];
            input.read(bytes);
            for(byte b : bytes) {
                char k = (char) b;
                stringBuilder.append(k);
            }

            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
