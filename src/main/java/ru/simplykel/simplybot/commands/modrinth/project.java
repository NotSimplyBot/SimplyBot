package ru.simplykel.simplybot.commands.modrinth;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import ru.simplykel.simplybot.Colors;
import ru.simplykel.simplybot.Main;
import ru.simplykel.simplybot.api.modrinth.entity.Project;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;

public class project {
    private ArrayList<ItemComponent>  buttons;
    private TextChannel channel;
    private EmbedBuilder help = new EmbedBuilder().setTitle("Информация о проекте из Modrinth")
            .setColor(new Colors().GREEN)
            .setDescription("Узнайте базовую информацию о проекте")
            .addField("Пример:","```\n" +
                    Main.settings.prefix+ "project Sodium" +
                    "\n```",false);
    public project(MessageReceivedEvent event, String[] args){
        channel = event.getChannel().asTextChannel();
        if(args.length < 2){
            event.getMessage().replyEmbeds(help.build()).mentionRepliedUser(false).queue();
            return;
        }
        String id = event.getMessage().getContentDisplay()
                .replace(Main.settings.prefix+"project ", "").replace(Main.settings.prefix+"project", "")
                .replace(Main.settings.prefix+"проект ", "").replace(Main.settings.prefix+"проект", "");
        EmbedBuilder response = embed(id);
        if(response == null) return;
        event.getMessage().replyEmbeds(response.build()).addActionRow(buttons).mentionRepliedUser(false).queue();

    }
    public project(SlashCommandInteractionEvent event){
        channel = event.getChannel().asTextChannel();
        String id = event.getOption("id").getAsString();
        EmbedBuilder response = embed(id);
        if(response == null) return;
        event.replyEmbeds(response.build()).addActionRow(buttons).queue();
    }
    public EmbedBuilder embed(String id){
        try {
            JSONObject json = Main.getJSON(Main.settings.ModrinthAPI+"project/"+id);
            Project mod = new Project(json);
            EmbedBuilder embed = new EmbedBuilder().setColor(new Color(0x1bd96a)).setTitle(mod.title).setThumbnail(mod.icon);
            embed.setDescription(mod.description);
            if(mod.moderatorMessage != null){
                embed.addField("Сообщение модератора", "> "+mod.moderatorMessage, false);
            }
            embed.addField("Статистика", "> Скачивания: "+mod.downloads+"\n" +
                    "> Подписчиков: "+mod.followers,false);
            embed.addField("Тип проекта", "> "+mod.type,false);
            embed.addField("Лицензия", "> ["+mod.licenceName+"]("+mod.licenceURL+")", false);
            ArrayList<ItemComponent> urls = new ArrayList<ItemComponent>();
            urls.add(net.dv8tion.jda.api.interactions.components.buttons.Button.link(mod.homePage, "Modrinth"));
            if(mod.discord != null){
                urls.add(net.dv8tion.jda.api.interactions.components.buttons.Button.link(mod.discord, "Discord"));
            }
            if(mod.wiki != null){
                urls.add(net.dv8tion.jda.api.interactions.components.buttons.Button.link(mod.wiki, "Wiki"));
            }
            if(mod.source != null){
                urls.add(net.dv8tion.jda.api.interactions.components.buttons.Button.link(mod.source, "Исходный код"));
            }
            if(mod.issues != null){
                urls.add(Button.link(mod.issues, "Баг-репорт"));
            }
            buttons = urls;
            return embed;
        }catch(Exception err){
            Main.error(err.getMessage(), channel);
            return null;
        }
    }
}
