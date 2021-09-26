package fr.crypenter.twitchapi;

import fr.crypenter.twitchapi.bot.TwitchBot;
import fr.crypenter.twitchapi.channel.TwitchChannel;
import fr.crypenter.twitchapi.entities.User;
import fr.crypenter.twitchapi.events.EventMessageReceived;

public class EventMessageExample implements EventMessageReceived {
    @Override
    public void onMessageReceived(TwitchBot twitchBot, TwitchChannel twitchChannel, String message, User user) {
        System.out.println(message);
        if(message.startsWith("!clear")) {
            System.out.println("CLEAR");
            twitchBot.clearChannel(twitchChannel);
        }
    }
}
