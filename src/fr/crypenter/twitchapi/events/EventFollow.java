package fr.crypenter.twitchapi.events;

import fr.crypenter.twitchapi.bot.TwitchBot;
import fr.crypenter.twitchapi.channel.TwitchChannel;
import fr.crypenter.twitchapi.entities.User;

public interface EventFollow {
    void onFollow(TwitchBot twitchBot, TwitchChannel twitchChannel, User follower);
}
