package fr.crypenter.twitchapi.events;

import fr.crypenter.twitchapi.bot.TwitchBot;
import fr.crypenter.twitchapi.channel.TwitchChannel;
import fr.crypenter.twitchapi.entities.User;

public interface EventSubscriber {
    void onSub(TwitchBot twitchBot, TwitchChannel twitchChannel, User subscriber);
}
