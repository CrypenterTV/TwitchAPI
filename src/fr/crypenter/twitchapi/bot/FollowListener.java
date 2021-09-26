package fr.crypenter.twitchapi.bot;

import fr.crypenter.twitchapi.entities.User;
import fr.crypenter.twitchapi.events.EventFollow;

import java.util.ArrayList;
import java.util.List;

public class FollowListener extends Thread {

    private TwitchBot twitchBot;

    private List<String> followers = new ArrayList<>();

    private boolean started = false;

    public FollowListener(TwitchBot twitchBot) {
        this.twitchBot = twitchBot;
    }

    @Override
    public void run() {
        started = true;
        try {

            while(true) {

                if(followers.isEmpty()) {

                    if(twitchBot.getLastFollowers() == null) {
                        continue;
                    }

                    followers.addAll(twitchBot.getLastFollowers());
                    Thread.sleep(1000);
                }

                List<String> oldList = new ArrayList<>(followers);
                followers.clear();
                followers.addAll(twitchBot.getLastFollowers());

                if(oldList.get(1).equals(followers.get(0))) {
                    continue;
                }

                if(!oldList.get(0).equals(followers.get(0))) {
                    for(EventFollow eventFollow : twitchBot.getEventFollowList()) {
                        eventFollow.onFollow(twitchBot, twitchBot.getTwitchChannel(), new User(followers.get(0)));
                    }
                }

                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TwitchBot getTwitchBot() {
        return twitchBot;
    }

    public boolean isStarted() {
        return started;
    }
}
