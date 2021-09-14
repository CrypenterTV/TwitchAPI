package fr.crypenter.twitchapi;

import fr.crypenter.twitchapi.bot.TwitchBot;
import fr.crypenter.twitchapi.channel.TwitchChannel;

public class Test {

    public static void main(String[] args) {

        TwitchBot twitchBot = new TwitchBot("CrypenterBot", "oauth:mmh0v5ll4njgxj6afyc5fxp6gl7y3f", 500);
        TwitchChannel twitchChannel = new TwitchChannel("ooo_globule_ooo");
        twitchBot.connect(twitchChannel);

        while(true) {
            if(twitchBot.isConnected()) {
                break;
            }
        }



        twitchBot.sendMessage("Coucou", twitchChannel);

        twitchBot.addMessageListener(new EventMessage());


    }

}
