package ru.simplykel.simplybot.commands.reworlds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.simplykel.simplybot.Main;
import org.json.JSONObject;

import java.awt.*;

public class online {

    private TextChannel channel;

    public online(MessageReceivedEvent event, String[] args){
        EmbedBuilder response = embed();
        if(response == null) return;
        event.getMessage().replyEmbeds(response.build()).mentionRepliedUser(false).queue();
    }
    public online(SlashCommandInteractionEvent event){
        channel = event.getChannel().asTextChannel();
        EmbedBuilder response = embed();
        if(response == null) return;
        event.replyEmbeds(response.build()).queue();
    }
    public EmbedBuilder embed(){
        try{
            JSONObject onlineAPI = Main.getJSON(Main.settings.MainAPI+"/rw/players");
            if(!onlineAPI.isNull("error")){
                Main.error(onlineAPI.getJSONObject("error").getString("message"), channel);
                return null;
            }
            EmbedBuilder info = new EmbedBuilder().setColor(new Color(0x9CC9FF)).setTitle("Онлайн сервера");
            info.addField("Общее кол-во онлайна", "> "+onlineAPI.getJSONArray("all").length(), false)
                    .addField("Онлайн основного сервера", "> "+onlineAPI.getJSONArray("main").length(), false)
                    .addField("Онлайн мира ферм", "> "+onlineAPI.getJSONArray("farm").length(), false);
            return info;
        } catch (Exception error){
            Main.error(error.getMessage(), channel);
            return null;
        }
    }
}
