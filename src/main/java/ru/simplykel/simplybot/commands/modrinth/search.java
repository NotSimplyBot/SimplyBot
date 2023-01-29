package ru.simplykel.simplybot.commands.modrinth;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.simplykel.simplybot.Colors;
import ru.simplykel.simplybot.Main;
import ru.simplykel.simplybot.api.modrinth.entity.Project;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;

public class search {
    private TextChannel channel;
    EmbedBuilder helpEmbed = new EmbedBuilder()
            .setTitle("Поиск проектов на Modrinth")
            .setDescription("Узнайте базовую информацию о проекте")
            .addField("Пример:","```\n" +
                    Main.settings.prefix+ "search discord rpc" +
                    "\n```",false)
            .setColor(new Colors().GREEN);
    public search(MessageReceivedEvent event, String[] args){
        channel = event.getChannel().asTextChannel();
        if(args.length < 2){
            event.getMessage().replyEmbeds(helpEmbed.build()).mentionRepliedUser(false).queue();
            return;
        }
        String id = event.getMessage().getContentDisplay()
                .replace(Main.settings.prefix+"search ", "").replace(Main.settings.prefix+"search", "")
                .replace(Main.settings.prefix+"поиск ", "").replace(Main.settings.prefix+"поиск", "");
        EmbedBuilder response = embed(id);
        if(response == null) return;

        event.getMessage().replyEmbeds(response.build()).mentionRepliedUser(false).queue();

    }
    public search(SlashCommandInteractionEvent event){
        channel = event.getChannel().asTextChannel();
        String query = event.getOption("query").getAsString();
        event.deferReply().queue(response -> {
            EmbedBuilder responseEmbed = embed(query);
            if(response == null) { return; }
            response.editOriginalEmbeds(responseEmbed.build()).queue();
        });
    }
    public EmbedBuilder embed(String query) {
        try {
            JSONObject json = Main.getJSON(Main.settings.ModrinthAPI+"search?limit=5&query="+query.replace(" ", "%20"));
            JSONArray hits = json.getJSONArray("hits");
            EmbedBuilder embedResults = new EmbedBuilder().setTitle("Результаты поиска").setColor(new Color(0x1bd96a));
            for(int i = 0; i<hits.length(); i++){
                JSONObject projectInfo = hits.getJSONObject(i);
                JSONObject projectInfoAPI = Main.getJSON(Main.settings.ModrinthAPI+"project/"+projectInfo.getString("project_id"));

                Project project = new Project(projectInfoAPI);
                embedResults.addField(project.title, "> "+project.description+"\n> Followers: "+project.followers + " Downloads: " + project.downloads + "\n> `"+Main.settings.prefix+"project "+project.id+"`", false);
            };
            return embedResults;
        } catch(Exception error){
            Main.error(error.getMessage(), channel);
            return null;
        }
    }
}
