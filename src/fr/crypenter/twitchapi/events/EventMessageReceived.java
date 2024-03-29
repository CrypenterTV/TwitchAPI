package fr.crypenter.twitchapi.events;

import fr.crypenter.twitchapi.bot.TwitchBot;
import fr.crypenter.twitchapi.channel.TwitchChannel;
import fr.crypenter.twitchapi.entities.User;

public interface EventMessageReceived {
    void onMessageReceived(TwitchBot twitchBot, TwitchChannel twitchChannel, String message, User user);
}
