package fr.crypenter.twitchapi.config;

import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {

    private static YamlConfiguration configuration;
    private static File configFile;

    public static void initConfig() {

        configuration = new YamlConfiguration();

        try {

            configFile = new File("twitchapi.yml");

            if(!configFile.exists()) {
                configFile.createNewFile();
                configuration.load(configFile);
                System.out.println("The config file has been generated at " + configFile.getAbsolutePath());
                return;
            }

            System.out.println("The config file has been generated at " + configFile.getAbsolutePath());

            configuration.load(configFile);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static File getConfigFile() {
        return configFile;
    }

    public static YamlConfiguration getConfiguration() {
        return configuration;
    }
}
