package ru.simplykel.simplybot.commands.information;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import ru.simplykel.simplybot.Colors;
import ru.simplykel.simplybot.Main;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Change_log {
    String version = "3.0.1";
    FileUpload fileUpload;
    public Change_log(MessageReceivedEvent event, String[] args){
        if(args.length >= 2) version = args[1];
        event.getMessage().replyEmbeds(change(version).build()).addFiles(fileUpload).mentionRepliedUser(false).queue();
    }
    public Change_log(SlashCommandInteractionEvent event){
        if(event.getOption("version") != null) version = event.getOption("version").getAsString();
        event.replyEmbeds(change(version).build()).addFiles(fileUpload).queue();
    }
    public EmbedBuilder change(String version){
        EmbedBuilder builder = new EmbedBuilder();
        try {
            InputStream file = getClass().getResourceAsStream("/versions/" + version + ".json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file));
            StringBuilder resultBuilder = new StringBuilder("");
            JSONObject update;
            try {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    resultBuilder.append(line);
                }
                update = new JSONObject(resultBuilder.toString());

                builder.setColor(new Colors().OLD_LAVENDER)
                        .setTitle(update.getString("codename") + " • " + update.getString("number"));
                if(!update.isNull("description")){
                    builder.setDescription("```\n"+update.getString("description")+"\n```");
                }
                if(!update.isNull("icon")){
                    builder.setThumbnail("attachment://icon"+update.getJSONObject("icon").getString("image"));
                    fileUpload = FileUpload.fromData(new File(Main.settings.folder+update.getJSONObject("icon").getString("file")), "icon"+update.getJSONObject("icon").getString("image"));
                }
                for(int i=0;i<update.getJSONArray("change").length();i++){
                    StringBuilder sb = new StringBuilder();
                    JSONObject changeOMG = update.getJSONArray("change").getJSONObject(i);
                    for(int i1=0;i1<changeOMG.getJSONArray("description").length();i1++){
                        sb.append("\n> • ").append(changeOMG.getJSONArray("description").get(i1));
                    }
                    builder.addField(changeOMG.getString("title"), sb.toString(), false);
                }

            } catch (Exception ex) {
                builder.setColor(new Colors().RED)
                        .setDescription("```"+ex.getMessage()+"```");
                ex.printStackTrace();
                if(Main.settings.developer){
                    builder.setImage("https://cdn.discordapp.com/attachments/872420528781148162/1021656434234114048/20220920_012050.jpg")
                            .setColor(new Color(0xffd035))
                            .setFooter("Картинка от YonKaGor / Twitter: @YonKaGore");
                }
            }
        } catch (NullPointerException error){
            builder.setColor(new Colors().RED)
                    .setDescription("Данная версия не была найдена!\n```\n"+error.getMessage()+"\n```");
            if(Main.settings.developer){
                builder.setImage("https://cdn.discordapp.com/attachments/872420528781148162/1021656434234114048/20220920_012050.jpg")
                        .setColor(new Color(0xffd035))
                        .setFooter("Картинка от YonKaGor / Twitter: @YonKaGore");
            }
        }
        return builder;
    }
}
