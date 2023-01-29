package ru.simplykel.simplybot.commands.reworlds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.simplykel.simplybot.Main;
import ru.simplykel.simplybot.api.reworlds.entity.Player;
import org.json.JSONObject;

import java.awt.*;

public class player {

    private TextChannel channel;
    EmbedBuilder embedHelp = new EmbedBuilder().setTitle("Информация игрока сервера RevolutionWorlds")
            .setColor(new Color(0x9CC9FF))
            .setDescription("Узнайте что-то про игрока")
            .addField("Пример", "```\n"+ Main.settings.prefix+"rw:player Alinochka\n```", false);
    public player(MessageReceivedEvent event, String[] args){
        if(args.length < 2){
            event.getMessage().replyEmbeds(embedHelp.build()).mentionRepliedUser(false).queue();
            return;
        }
        String player = event.getMessage().getContentDisplay()
                .replace(Main.settings.prefix+"rw:player ", "").replace(Main.settings.prefix+"rw:player", "");
        EmbedBuilder response = embed(player);
        if(response == null) return;
        event.getMessage().replyEmbeds(response.build()).mentionRepliedUser(false).queue();
    }
    public player(SlashCommandInteractionEvent event){
        channel = event.getChannel().asTextChannel();
        String player = event.getOption("player").getAsString();
        EmbedBuilder response = embed(player);
        if(response == null) return;
        event.replyEmbeds(response.build()).queue();
    }
    public EmbedBuilder embed(String player){
        try{
            JSONObject playerAPI = Main.getJSON(Main.settings.MainAPI+"/rw/player?player="+player);
            if(!playerAPI.isNull("error")){
                Main.error(playerAPI.getJSONObject("error").getString("message"), channel);
                return null;
            }
            Player playerInfo = new Player(playerAPI);
            EmbedBuilder info = new EmbedBuilder().setColor(new Color(0x9CC9FF)).setTitle("Информация игрока "+player);
            if(playerInfo.isOnline()){
                info.addField("На сервере", "> "+playerInfo.getWorldType(), false)
                        .addField("Зашёл", "> <t:"+playerInfo.getLastPlaying()+":f>", false)
                        .addField("Наиграл", "> " + getTimestamp(playerInfo.getPlayTime() * 1000), false);
            } else {
                info.setDescription("Игрок не в сети")
                        .addField("Заходил последний раз", "> <t:"+playerInfo.getLastPlaying()+":f>", false)
                        .addField("Наиграл", "> " + getTimestamp(playerInfo.getPlayTime() * 1000), false);
            }
            return info;
        } catch (Exception error){
            Main.error(error.getMessage(), channel);
            return null;
        }
    }
    public static String getTimestamp(long milliseconds)
    {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        int days    = (int) ((milliseconds / (1000 * 60 * 60 * 24)) % 365);

        if (days > 0)
            return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
        else if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }
}
