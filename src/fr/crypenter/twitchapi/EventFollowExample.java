package fr.crypenter.twitchapi;

import fr.crypenter.twitchapi.bot.TwitchBot;
import fr.crypenter.twitchapi.channel.TwitchChannel;
import fr.crypenter.twitchapi.entities.User;
import fr.crypenter.twitchapi.events.EventFollow;

public class EventFollowExample implements EventFollow {
    @Override
    public void onFollow(TwitchBot twitchBot, TwitchChannel twitchChannel, User follower) {
        System.out.println("NEW FOLLOWER !!!! : " + follower.getName());
        twitchChannel.sendMessage("Thanks " + follower.getName() + " for the follow ! TakeRNG");
    }
}
