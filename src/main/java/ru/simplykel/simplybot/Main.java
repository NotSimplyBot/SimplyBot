package ru.simplykel.simplybot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.simplykel.simplybot.listeners.Messages;
import ru.simplykel.simplybot.listeners.Interaction;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;

public class Main {
    public static Logger LOG = LoggerFactory.getLogger("SimplyBot");
    public static Logger LOG_INFO = LoggerFactory.getLogger("API Information");
    public static JDA bot;
    public static Settings settings;

    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        LOG.info("Доброго времени суток! UwU");
        settings = new Settings();
        bot = JDABuilder.createDefault(settings.token,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MEMBERS)
                .enableCache(CacheFlag.ACTIVITY)
                .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
                .setActivity(Activity.watching(settings.prefix + "help"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(new Messages())
                .build();
        bot.awaitReady();
        bot.addEventListener(new Interaction(bot));
    }

    public static void error(String error, TextChannel channel) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Произошла ошибка!")
                .setColor(new Colors().RED)
                .setDescription("```\n" + error + "\n```");
        channel.sendMessageEmbeds(embed.build()).queue();
    }

    public static JSONObject getJSON(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());
        LOG_INFO.info("JSON: " + json.toString());
        return json;
    }
}
