package fr.crypenter.twitchapi.channel;

public class TwitchChannel {

    private String name;

    public TwitchChannel(String name) {
        this.name = "#" + name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
