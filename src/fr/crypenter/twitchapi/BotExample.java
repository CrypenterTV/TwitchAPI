package fr.crypenter.twitchapi;

import fr.crypenter.twitchapi.bot.TwitchBot;
import fr.crypenter.twitchapi.channel.TwitchChannel;


public class BotExample {

    public static void main(String[] args) throws Exception {


        TwitchBot twitchBot = new TwitchBot("YourBotName", "Your Oauth Token", 500);
        TwitchChannel twitchChannel = new TwitchChannel("yourChannel", twitchBot);
        //CLIENT_ID AND CLIENT_SECRET
        twitchBot.setClientId("yourClientId");
        twitchBot.setClientSecret("yourClientSecret");

        //OAUTH ACCESS TOKEN
        twitchBot.setAccessToken("yourClientAccessToken");
        //REFRESH CODE TO REFRESH THE ACCESS TOKEN
        twitchBot.setRefreshToken("yourRefreshToken");

        //YOUR TWITCH USER ID
        twitchBot.setUserId("yourTwitchUserId");

        twitchBot.startRefreshingToken();

        System.out.println("Number of subscribers : " + twitchBot.getSubscribersNum());

        for(String s : twitchBot.getLastSubscribers()) {
            System.out.println(s);
        }

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


        System.out.println("TOTAL FOLLOWERS : " + twitchBot.getFollowersNum());


        twitchBot.addFollowListener(new EventFollowExample());



    }






}
