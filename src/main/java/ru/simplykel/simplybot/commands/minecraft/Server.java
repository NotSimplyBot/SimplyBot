package ru.simplykel.simplybot.commands.minecraft;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import ru.simplykel.simplybot.Colors;
import ru.simplykel.simplybot.Main;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;


public class Server {
    public TextChannel channel;
    public long id;
    static FileUpload icon;
    public EmbedBuilder help = new EmbedBuilder().setTitle("Информация Minecraft сервера")
            .setColor(new Colors().OLD_LAVENDER)
            .setDescription("Эта команда покажет некоторую информацию о сервере\n" +
                    "По типу версии, протокола и онлайна")
            .addField("Пример:",
                    "```\n"+Main.settings.prefix+"server play.hypixel.net\n```",
                    false);
    public Server(MessageReceivedEvent event, String[] args){
        channel = event.getChannel().asTextChannel();
        id = event.getMessageIdLong();
        if(args.length < 2){
            event.getMessage().replyEmbeds(help.build()).mentionRepliedUser(false).queue();
            return;
        }
        int protocol = Main.settings.protocol;
        boolean userProtocol = false;
        if(args.length >= 3){
            protocol = Integer.parseInt(args[2]);
            userProtocol = true;
        }
        EmbedBuilder response = embed(args[1], protocol, userProtocol, event.getGuild());
        if(response == null) return;
        event.getMessage().replyEmbeds(response.build())
                .addFiles(icon)
                .mentionRepliedUser(false).queue();
    }
    public Server(SlashCommandInteractionEvent event){
        channel = event.getChannel().asTextChannel();
        id = event.getIdLong();
        if(event.getOption("address") == null){
            event.replyEmbeds(help.build()).setEphemeral(true).queue();
            return;
        }
        int protocol = Main.settings.protocol;
        boolean userProtocol = false;
        String address = event.getOption("address").getAsString();
        if(event.getOption("protocol") != null){
            protocol = event.getOption("protocol").getAsInt();
            userProtocol = true;
        }
        int finalProtocol = protocol;
        boolean finalUserProtocol = userProtocol;
        event.deferReply().queue(response -> {
            EmbedBuilder response1 = embed(address, finalProtocol, finalUserProtocol, event.getGuild());
            if(response1 == null){
                response.editOriginal("У вас скорее всего возникла ошибка, если сообщение ошибки нет, сообщите разработчику!").queue();
                return;
            };
            response.editOriginalEmbeds(response1.build())
                    .setFiles(icon).queue();
        });
    }
    public EmbedBuilder embed(String ip, int protocol, boolean protocolUser, Guild guild) {
        ru.simplykel.simplybot.api.Server information;
        String domain = !ip.contains(":") ? ip : ip.split(":")[0];
        int port = !ip.contains(":") ? 25565 : Integer.parseInt(ip.split(":")[1]);
        try{
            if(protocolUser){
                information = new ru.simplykel.simplybot.api.Server(domain, port, protocol);
                information.connect(15000);
            } else {
                information = new ru.simplykel.simplybot.api.Server(domain, port, Main.settings.protocol);
                information.connect(15000);
                if(!information.getProtocol().equals(Main.settings.protocol)){
                    final int protocolServer = information.getProtocol();
                    information = new ru.simplykel.simplybot.api.Server(domain, port, protocolServer);
                    information.connect(15000);
                }
            }

        } catch (Exception e){
            if(domain.equals(e.getMessage())){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/servers.json")));
                StringBuilder resultBuilder = new StringBuilder("");
                JSONObject list;
                try {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        resultBuilder.append(line);
                    }
//                    Main.LOG.warn(resultBuilder.toString());
                    list = new JSONObject(resultBuilder.toString());
                } catch (IOException ex) {
                    Main.error(ex.getMessage(), channel);
                    return null;
                }
//                Main.LOG.warn(list.toString());
                if(list.isNull(domain)){
                    Main.error("IP адрес не был найден, ни в DNS, ни в резервном файле серверов", channel);
                    return null;
                } else {
                    EmbedBuilder amogus = embed(list.getJSONObject(domain).getString("address"), protocol, protocolUser, guild);
                    if(amogus != null){
                        return amogus;
                    } else {
                        return null;
                    }
                }
            }else{
                Main.error(e.getMessage(), channel);
                return null;
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Информация сервера");
        try{
            var component = GsonComponentSerializer.gson().deserialize(information.getDescription());
            var formatted = PlainTextComponentSerializer.plainText().serialize(component);
            for(int i = 0; i < format.length; i = i+1) {
                formatted = formatted.replace(format[i], "");
            }
            builder.setDescription("```\n"+formatted+"\n```");
        } catch(Exception err){
            var desc = information.getDescription().replace("\\n", "\n");
            for(int i = 0; i < format.length; i = i+1) {
                desc = desc.replace(format[i], "");
            }
            builder.setDescription("```\n"+desc+"\n```");
        }
        if(guild.getPublicRole().hasPermission(Permission.MESSAGE_EXT_EMOJI)){
            builder.addField("<:Controller:920301529607577641> Версия", information.getVersion(),true);
            builder.addField("<:Servers:920297742230769694> Протокол", String.valueOf(information.getProtocol()),true);
            builder.addField("<:Ping:920298384470978600> Порт подключения", String.valueOf(port), true);
            if(information.getOnline() == null && information.getMax() == null){
                builder.addField("<:FriendsIcon:920297662954221620> Игроки","*???*",true);
            } else if(information.getOnline() == null){
                builder.addField("<:FriendsIcon:920297662954221620> Игроки",""+information.getMax(),true);
            } else if(information.getMax() == null){
                builder.addField("<:FriendsIcon:920297662954221620> Игроки",information.getOnline()+"",true);
            } else {
                builder.addField("<:FriendsIcon:920297662954221620> Игроки",information.getOnline() + " • " + information.getMax(),true);
            }
            builder.addField("<:Ping:920298384470978600> Пинг", (information.getPing() / 10) +"ms", true);
            if(!domain.equals(information.getHostAddress())){
                builder.addField("<:Servers:920297742230769694> Адрес", domain+"\n"+information.getHostAddress(), true);
            } else {
                builder.addField("<:Servers:920297742230769694> Адрес", information.getHostAddress(), true);
            }

            if(information.getProtocol() >= 760){
                if(information.getSecureChat() || information.getPreviewChat()){
                    String warns = "";
                    if(information.getSecureChat()){
                        warns = warns+"> Сервер не позволяет подключиться без открытого ключа, подписаным Mojang Studios.\n";
                    }
                    if(information.getPreviewChat()){
                        warns = warns+"> Сервер не позволяет пользователям использовать VPN или прокси.";
                    }
                    builder.addField("<:VeksterBan_molotkom:899209323761242163> Ограничения", warns, false);
                }
            }
        } else {
            builder.addField("Версия", information.getVersion(),true);
            builder.addField("Протокол", String.valueOf(information.getProtocol()),true);
            builder.addField("Порт подключения", String.valueOf(port), true);
            if(information.getOnline() == null && information.getMax() == null){
                builder.addField("Игроки","*???*",true);
            } else if(information.getOnline() == null){
                builder.addField("Игроки",""+information.getMax(),true);
            } else if(information.getMax() == null){
                builder.addField("Игроки",information.getOnline()+"",true);
            } else {
                builder.addField("Игроки",information.getOnline() + " • " + information.getMax(),true);
            }
            builder.addField("Пинг", (information.getPing() / 10) +"ms", true);
            if(!domain.equals(information.getHostAddress())){
                builder.addField("Адрес", domain+"\n"+information.getHostAddress(), true);
            } else {
                builder.addField("Адрес", information.getHostAddress(), true);
            }
            if(information.getProtocol() >= 760){
                if(information.getSecureChat() || information.getPreviewChat()){
                    String warns = "";
                    if(information.getSecureChat()){
                        warns = warns+"> Сервер не позволяет подключиться без открытого ключа, подписаным Mojang Studios.\n";
                    }
                    if(information.getPreviewChat()){
                        warns = warns+"> Сервер не позволяет пользователям использовать VPN или прокси.";
                    }
                    builder.addField("Ограничения", warns, false);
                }
            }
        }

        if(information.getFavicon() != null){
            String favicon = information.getFavicon().replace("data:image/png;base64,", "");
            String IPRename = domain.replace(".", "-");
            try{
                Path destinationFile = Paths.get(Main.settings.folder+"cache/icons-servers", IPRename+".png");
                Files.write(destinationFile, image(favicon));

                File file = new File(Main.settings.folder+"cache/icons-servers/"+IPRename+".png");
                BufferedImage image = ImageIO.read(file);
                int clr = image.getRGB((image.getHeight()/2), (image.getWidth()/2));
                int red =   (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue =   clr & 0x000000ff;
                builder.setColor(new Color(red, green, blue));
                builder.setThumbnail("attachment://"+IPRename+".png");
                icon = FileUpload.fromData(new File(Main.settings.folder+"cache/icons-servers/"+IPRename+".png"), IPRename+".png");
            } catch (Exception e) {

            }
        }
        return builder;
    }
    public static String[] format =  {
            "§4",
            "§c",
            "§6",
            "§e",
            "§z",
            "§a",
            "§b",
            "§3",
            "§1",
            "§9",
            "§d",
            "§5",
            "§f",
            "§7",
            "§8",
            "§0",
            "§r",
            "§l",
            "§o",
            "§n",
            "§m",
            "§k"
    };
    public static byte[] image(String base64){
        byte[] decodedImg = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
        return decodedImg;
    }
    public static byte[] image(byte[] base64){
        byte[] decodedImg = Base64.getDecoder().decode(base64);
        return decodedImg;
    }
}
