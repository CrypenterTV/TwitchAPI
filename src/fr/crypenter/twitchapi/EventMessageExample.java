package fr.crypenter.twitchapi;

import fr.crypenter.twitchapi.bot.TwitchBot;
import fr.crypenter.twitchapi.entities.User;
import fr.crypenter.twitchapi.events.EventMessageReceived;

public class EventMessageExample implements EventMessageReceived {
    @Override
    public void onMessageReceived(TwitchBot twitchBot, String message, User user) {
        System.out.print("NEW MESSAGE RECEIVED @" + user.getName() + " : " + message);
    }
}
