package ru.simplykel.simplybot;

import java.io.*;
import java.util.Properties;

public class Settings {
    public String token;
    public String prefix;
    public boolean enable;
    public String folder;
    public int protocol;
    public boolean developer;
    public String MainAPI;
    public String ModrinthAPI;
    public InputStream stream;

    public Settings() throws IOException {
        if (System.getProperty("os.name").startsWith("Windows")) {
            folder = System.getProperty("user.home") + "\\SimplyBot\\";
        } else {
            folder = System.getProperty("user.home") + "/SimplyBot/";
        }
        Main.LOG.info("Bot folder: " + folder);
        File file = new File(folder + "bot.properties");
        if (file.exists()) {
            stream = new FileInputStream(file);
        } else {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            stream = loader.getResourceAsStream("bot.properties");
        }
        Properties properties = new Properties();
        properties.load(stream);
        token = properties.getProperty("token", "");
        prefix = properties.getProperty("prefix", "!");
        enable = Boolean.parseBoolean(properties.getProperty("enable", "true"));
        // Прочее
        protocol = Integer.parseInt(properties.getProperty("protocol", "760"));
        developer = Boolean.parseBoolean(properties.getProperty("developer", "true"));
        MainAPI = properties.getProperty("main-api", "http://localhost");
        ModrinthAPI = properties.getProperty("modrinth-api", "https://staging-api.modrinth.com/v2/");
    }
}
