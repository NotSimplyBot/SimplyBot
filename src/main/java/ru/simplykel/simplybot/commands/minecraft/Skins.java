package ru.simplykel.simplybot.commands.minecraft;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import ru.simplykel.simplybot.Colors;
import ru.simplykel.simplybot.Main;
import org.json.JSONObject;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.File;
import java.io.IOException;

public class Skins {
    EmbedBuilder help = new EmbedBuilder().setTitle("Команда `" + Main.settings.prefix + "skins`")
            .setDescription("Вы можете проверить свой скин, или чей-то скин благодаря этой команды!\n" +
                    "Поддерживаются популярные API скинов:\n" +
                    "• Локальные скины\n" +
                    "• Minecraft: Java Edition\n" +
                    "• ElyBy\n" +
                    "• LittleSkin\n" +
                    "• BlessingSkin\n" +
                    "• GlitchlessGames")
            .setColor(new Colors().OLD_LAVENDER)
            .addField("Пример", "```\n" +
                    Main.settings.prefix + "skins Simply_Kel\n" +
                    Main.settings.prefix + "skins Alinochka_Maid ~id=0 ~head\n" +
                    "```", false)
            .setThumbnail("attachment://skin-render.png");
    private FileUpload headFile;
    private FileUpload bodyFile;
    private Button buttonUrl;

    public Skins(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            event.getMessage().replyEmbeds(help.build())
                    .addFiles(FileUpload.fromData(new File(Main.settings.folder + "assets/icons/skin-render.png")))
                    .mentionRepliedUser(false).queue();
            return;
        }
        String name = "";
        int api = -1;
        String URL = "";
        boolean isURL = false;
        boolean head = false;
        if (args[1].startsWith("http://") || args[1].startsWith("https://")) {
            isURL = true;
            URL = args[1];
        } else {
            name = args[1];
        }
        StringBuilder url = new StringBuilder(Main.settings.MainAPI + "/skin/render?name=").append(name);
        if (isURL) {
            url = new StringBuilder(Main.settings.MainAPI + "/skin/render/url?url=").append(URL);
        }
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "~sb", "~simplybot" -> {
                    if (api == -1 && isURL == false) {
                        api = 0;
                        url.append("&api=").append(api);
                    }
                }
                case "~m", "~mojang" -> {
                    if (api == -1 && isURL == false) {
                        api = 1;
                        url.append("&api=").append(api);
                    }
                }
                case "~eb", "~elyby" -> {
                    if (api == -1 && isURL == false) {
                        api = 2;
                        url.append("&api=").append(api);
                    }
                }
                case "~sm", "~skinme" -> {
                    if (api == -1 && isURL == false) {
                        api = 3;
                        url.append("&api=").append(api);
                    }
                }
                case "~ls", "~littleskin" -> {
                    if (api == -1 && isURL == false) {
                        api = 4;
                        url.append("&api=").append(api);
                    }
                }
                case "~bs", "~blessingskin" -> {
                    if (api == -1 && isURL == false) {
                        api = 5;
                        url.append("&api=").append(api);
                    }
                }
                case "~gg", "~glitchlessgames" -> {
                    if (api == -1 && isURL == false) {
                        api = 6;
                        url.append("&api=").append(api);
                    }
                }
                case "~prp", "~plasmorp" -> {
                    if (api == -1 && isURL == false) {
                        api = 7;
                        url.append("&api=").append(api);
                    }
                }
                case "~head" -> {
                    head = true;
                }
                case "~slim" -> {
                    url.append("&slim=true");
                }
            }
        }
        url.append("&head=").append(head);
        try {
            JSONObject json = Main.getJSON(url.toString());
            if (head) {
                event.getMessage().replyEmbeds(info(json, true).build())
                        .mentionRepliedUser(false)
                        .setFiles(headFile)
                        .addActionRow(buttonUrl)
                        .queue();
            } else {
                event.getMessage().replyEmbeds(info(json, false).build())
                        .mentionRepliedUser(false)
                        .setFiles(headFile, bodyFile)
                        .addActionRow(buttonUrl)
                        .queue();
            }
        } catch (IOException | InterruptedException e) {
            event.getMessage().reply("Произошла ошибка!");
            throw new RuntimeException(e);
        }
    }

    public Skins(SlashCommandInteractionEvent event) {
        String name = "";
        int api = -1;
        String URL = "";
        boolean isURL = false;
        boolean head = false;
        if ((event.getOption("name") == null) && (event.getOption("url") == null)) {
            event.replyEmbeds(help.build())
                    .addFiles(FileUpload.fromData(new File(Main.settings.folder + "assets/icons/skin-render.png")))
                    .mentionRepliedUser(false).queue();
        } else {
            if (event.getOption("name") != null) name = event.getOption("name").getAsString();
            else {
                isURL = true;
                URL = event.getOption("url").getAsString();
            }
        }
        if (event.getOption("api") != null) api = event.getOption("api").getAsInt();
        if (event.getOption("head") != null) head = event.getOption("head").getAsBoolean();
        StringBuilder url = new StringBuilder(Main.settings.MainAPI + "/skin/render?name=").append(name).append("&head=").append(head);
        if (isURL) {
            url = new StringBuilder(Main.settings.MainAPI + "/skin/render/url?url=").append(URL).append("&head=").append(head);
        }
        if (event.getOption("slim") != null) url.append("&slim=").append(event.getOption("slim").getAsBoolean());
        if (api != -1) url.append("&api=").append(api);

        boolean finalHead = head;
        StringBuilder finalUrl = url;
        event.deferReply().queue(response -> {
            try {
                JSONObject json = Main.getJSON(finalUrl.toString());
                if (finalHead) {
                    response.editOriginalEmbeds(info(json, finalHead).build())
                            .setFiles(headFile)
                            .setActionRow(buttonUrl)
                            .queue();
                } else {
                    response.editOriginalEmbeds(info(json, finalHead).build())
                            .setFiles(headFile, bodyFile)
                            .setActionRow(buttonUrl)
                            .queue();
                }
            } catch (IOException | InterruptedException e) {
                response.editOriginal("Произошла ошибка!");
                throw new RuntimeException(e);
            }

        });

    }

    public EmbedBuilder info(JSONObject json, boolean head) {
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

            if (head) {
                result.setImage("attachment://head.png");
                headFile = FileUpload.fromData(new File(json.getJSONObject("render").getString("local")), "head.png");
            } else {
                try {
                    JSONObject jsonHead = Main.getJSON(Main.settings.MainAPI + "/skin/render/url?url=" + json.getJSONObject("file").getString("web") + "&head=true");
                    result.setThumbnail("attachment://head.png");
                    headFile = FileUpload.fromData(new File(jsonHead.getJSONObject("render").getString("local")), "head.png");
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                buttonUrl = Button.link(json.getString("skin"), "Файл скина").withEmoji(Emoji.fromUnicode("👚"));
                result.setImage("attachment://body.png");
                bodyFile = FileUpload.fromData(new File(json.getJSONObject("render").getString("local")), "body.png");
            }
        }
        return result;
    }
}
