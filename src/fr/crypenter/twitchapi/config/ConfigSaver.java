package fr.crypenter.twitchapi.config;

import fr.crypenter.twitchapi.bot.TwitchBot;

public class ConfigSaver {

    public static void saveConfig(TwitchBot twitchBot) {

        ConfigManager.getConfiguration().set("client_id", twitchBot.getClientId());
        ConfigManager.getConfiguration().set("client_secret", twitchBot.getClientSecret());
        ConfigManager.getConfiguration().set("user_id", twitchBot.getUserId());
        ConfigManager.getConfiguration().set("access_token", twitchBot.getAccessToken());
        ConfigManager.getConfiguration().set("refresh_token", twitchBot.getRefreshToken());
        saveConfig();
    }

    public static void saveConfig() {
        try {
            ConfigManager.getConfiguration().save(ConfigManager.getConfigFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
