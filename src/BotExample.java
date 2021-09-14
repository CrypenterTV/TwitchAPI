import fr.crypenter.twitchapi.EventMessageExample;
import fr.crypenter.twitchapi.bot.TwitchBot;
import fr.crypenter.twitchapi.channel.TwitchChannel;

public class BotExample {

    public static void main(String[] args) {

        TwitchBot twitchBot = new TwitchBot("username", "yourAuthToken", 500);
        TwitchChannel twitchChannel = new TwitchChannel("yourTwitchChannel");

        twitchBot.connect(twitchChannel);

        //Wait for bot connection...

        while(true) {
            if(twitchBot.isConnected()) {
                break;
            }
        }

        //Send a message in a channel
        twitchBot.sendMessage("Hello World !", twitchChannel);
        //Register messages listener
        twitchBot.addMessageListener(new EventMessageExample());

    }

}
