package fr.crypenter.twitchapi.channel;


import fr.crypenter.twitchapi.bot.TwitchBot;


public class TwitchChannel {

    private String name;

    private TwitchBot bot;

    public TwitchChannel(String name, TwitchBot bot) {
        this.name = "#" + name;
        this.bot = bot;
    }

    public void clear() {
        bot.clearChannel(this);
    }

    public void sendMessage(String message) {
        bot.sendMessage(message, this);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
