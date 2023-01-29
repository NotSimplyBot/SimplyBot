package ru.simplykel.simplybot.commands.minecraft;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import ru.simplykel.simplybot.Colors;
import ru.simplykel.simplybot.Main;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class Capes {
    private FileUpload capeFile;
    EmbedBuilder helpEmbed = new EmbedBuilder().setTitle("Команда `"+ Main.settings.prefix+"capes`")
            .setDescription("С помощью этой команды вы можете посмотреть прекрасный`[возможно]` плащ игрока Minecraft.")
            .setColor(new Colors().OLD_LAVENDER)
            .addField("Доступные API",
                    "• Mojang\n" +
                            "• Minecraft Capes\n" +
                            "• Optifine\n" +
                            "• Cloaks+", false)
            .addField("Пример","```\n" +
                    Main.settings.prefix+"capes Alexey\n" +
                    Main.settings.prefix+"capes Simply_Kel ~mc\n" +
                    "```",false)
            .setThumbnail("attachment://cape-render.png")
            .setImage("attachment://cape-preview-render.png")
            .setFooter("*Нужна лицензия на нике!");
    public Capes(MessageReceivedEvent event, String[] args){
        if(args.length < 2){
            event.getMessage().replyEmbeds(helpEmbed.build())
                    .addFiles(FileUpload.fromData(new File(Main.settings.folder+"assets/icons/cape-render.png")))
                    .addFiles(FileUpload.fromData(new File(Main.settings.folder+"assets/icons/cape-preview-render.png")))
                    .mentionRepliedUser(false).queue();
            return;
        }
        String name = "";
        int api = -1;
        name = args[1];
        StringBuilder url = new StringBuilder(Main.settings.MainAPI + "/cape/render?name=").append(name);

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "~m", "~mojang" -> {
                    if (api == -1) {
                        api = 0;
                        url.append("&api=").append(api);
                    }
                }
                case "~mc", "~minecraftcapes" -> {
                    if (api == -1) {
                        api = 1;
                        url.append("&api=").append(api);
                    }
                }
                case "~op", "~optifine" -> {
                    if (api == -1) {
                        api = 2;
                        url.append("&api=").append(api);
                    }
                }
                case "~c+", "~cloaks+" -> {
                    if (api == -1) {
                        api = 3;
                        url.append("&api=").append(api);
                    }
                }
            }
        }
        try {
            JSONObject json = Main.getJSON(url.toString());

                event.getMessage().replyEmbeds(info(json).build())
                        .mentionRepliedUser(false)
                        .setFiles(capeFile)
                        .queue();

        } catch (IOException | InterruptedException e) {
            event.getMessage().reply("Произошла ошибка!");
            throw new RuntimeException(e);
        }
    }
    public Capes(SlashCommandInteractionEvent event){
        String name = "";
        int api = -1;
        name = event.getOption("name").getAsString();
        if (event.getOption("api") != null) api = event.getOption("api").getAsInt();
        StringBuilder url = new StringBuilder(Main.settings.MainAPI + "/cape/render?name=").append(name);
        if (api != -1) url.append("&api=").append(api);
        StringBuilder finalUrl = url;
        event.deferReply().queue(response -> {
            try {
                JSONObject json = Main.getJSON(finalUrl.toString());

                    response.editOriginalEmbeds(info(json).build())
                            .setFiles(capeFile)
                            .queue();

            } catch (IOException | InterruptedException e) {
                response.editOriginal("Произошла ошибка!");
                throw new RuntimeException(e);
            }

        });

    }
    public EmbedBuilder info(JSONObject json) {
        EmbedBuilder result = new EmbedBuilder();
        if (!json.isNull("error")) {
            JSONObject error = json.getJSONObject("error");
            result.setColor(new Colors().RED)
                    .setDescription("```\n" +
                            error.getInt("code") + " " +
                            error.getString("codename") + "\n" +
                            error.getString("message") + "\n" +
                            "```")
                    .setTitle("Произошла ошибка!");
        } else {
            result.setColor(new Colors().OLD_LAVENDER)
                    .setTitle(json.getString("nickname"))
                    .addField("API", "> " + json.getJSONObject("api").getString("name"), false);


                result.setImage("attachment://cape.png");
                capeFile = FileUpload.fromData(new File(json.getJSONObject("render").getString("local")), "cape.png");

        }
        return result;
    }
}
