package fr.crypenter.twitchapi.bot;

import fr.crypenter.twitchapi.entities.User;
import fr.crypenter.twitchapi.events.EventSubscriber;

import java.util.ArrayList;
import java.util.List;

public class SubscriberListener extends Thread {

    private TwitchBot twitchBot;

    private List<String> subscribers = new ArrayList<String>();

    private boolean started = false;

    public SubscriberListener(TwitchBot twitchBot) {
        this.twitchBot = twitchBot;
    }

    @Override
    public void run() {
        started = true;
        try {

            while(true) {

                if(subscribers.isEmpty()) {

                    if(twitchBot.getLastSubscribers() == null) {
                        continue;
                    }

                    subscribers.addAll(twitchBot.getLastSubscribers());
                    Thread.sleep(1000);
                }

                List<String> oldList = new ArrayList<>(subscribers);
                subscribers.clear();
                subscribers.addAll(twitchBot.getLastFollowers());

                if(oldList.get(1).equals(subscribers.get(0))) {
                    continue;
                }

                if(!oldList.get(0).equals(subscribers.get(0))) {
                    for(EventSubscriber eventSubscriber : twitchBot.getEventSubscriberList()) {
                        eventSubscriber.onSub(twitchBot, twitchBot.getTwitchChannel(), new User(subscribers.get(0)));
                    }
                }

                Thread.sleep(1000);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public List<String> getSubscribers() {
        return subscribers;
    }

    public TwitchBot getTwitchBot() {
        return twitchBot;
    }

    public boolean isStarted() {
        return started;
    }
}
