package ru.simplykel.simplybot.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.simplykel.simplybot.Main;
import og.kel.simplybot.commands.information.*;
import ru.simplykel.simplybot.commands.games.Ben;
import ru.simplykel.simplybot.commands.games.Me;
import ru.simplykel.simplybot.commands.information.Change_log;
import ru.simplykel.simplybot.commands.information.Help;
import ru.simplykel.simplybot.commands.information.Ping;
import ru.simplykel.simplybot.commands.minecraft.Capes;
import ru.simplykel.simplybot.commands.minecraft.Server;
import ru.simplykel.simplybot.commands.minecraft.Skins;
import ru.simplykel.simplybot.commands.modrinth.project;
import ru.simplykel.simplybot.commands.reworlds.online;
import ru.simplykel.simplybot.commands.reworlds.player;
import og.kel.simplybot.commands.util.*;
import og.kel.simplybot.commands.minecraft.*;
import og.kel.simplybot.commands.modrinth.*;
import og.kel.simplybot.commands.games.*;
import org.slf4j.LoggerFactory;
import ru.simplykel.simplybot.commands.modrinth.search;
import ru.simplykel.simplybot.commands.util.avatar;
import ru.simplykel.simplybot.commands.util.user;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class Messages extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        CompletableFuture.runAsync(() -> {
            if(!event.isFromGuild() && (!event.getAuthor().isBot() || !event.getAuthor().isSystem())) return;
            if(!event.getMessage().getContentDisplay().startsWith(Main.settings.prefix)) return;
            Message message = event.getMessage();
            String[] args = message.getContentRaw().trim().split(" ");
            String command = args[0].toLowerCase().replace(Main.settings.prefix, "");
            //What happened?
            boolean isUseCommand = false;
            switch (command) {
                case "help", "????????????" -> {
                    isUseCommand = true;
                    new Help(event);
                }
                case "ping", "????????" -> {
                    isUseCommand = true;
                    new Ping(event);
                }
                case "user", "profile", "????????????????????????", "??????????????" -> {
                    isUseCommand = true;
                    new user(event);
                }
                case "avatar", "????????????" -> {
                    isUseCommand = true;
                    new avatar(event);
                }
                case "ben", "??????" -> {
                    isUseCommand = true;
                    new Ben(event, args);
                }
                case "server", "????????????" -> {
                    isUseCommand = true;
                    new Server(event, args);
                }
                case "project", "????????????" -> {
                    isUseCommand = true;
                    new project(event, args);
                }
                case "search", "??????????" -> {
                    isUseCommand = true;
                    new search(event, args);
                }
                case "change-log", "change_log" -> {
                    isUseCommand = true;
                    new Change_log(event, args);
                }
                case "me" -> {
                    isUseCommand = true;
                    new Me(event, args);
                }
                case "skin", "skins" -> {
                    isUseCommand = true;
                    new Skins(event, args);
                }
                case "cape", "capes" -> {
                    isUseCommand = true;
                    new Capes(event, args);
                }
                case "rw:player" -> {
                    isUseCommand = true;
                    new player(event, args);
                }
                case "rw:online" -> {
                    isUseCommand = true;
                    new online(event, args);
                }
            }
            if(isUseCommand){
                StringBuilder txtFile = new StringBuilder("?????????????????????????? ??????????????:");
                txtFile.append("\n").append("????????????????????????:")
                        .append("\n").append("# ID: ").append(event.getAuthor().getId())
                        .append("\n").append("# ??????: ").append(event.getAuthor().getName());
                txtFile.append("\n").append("??????????????????:")
                        .append("\n").append("# ID: ").append(event.getMessage().getId())
                        .append("\n").append("# ??????????????: ").append(event.getMessage().getContentDisplay());
                txtFile.append("\n").append("????????????:")
                        .append("\n").append("# ID: ").append(event.getGuild().getId())
                        .append("\n").append("# ????????????????: ").append(event.getGuild().getName());
                try{
                    final Path configFile = Path.of(Main.settings.folder+"cache/log/"+event.getAuthor().getId()+"/"+event.getMessage().getId()+".txt");
                    Files.createDirectories(configFile.getParent());
                    Files.writeString(configFile, txtFile.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                LoggerFactory.getLogger("SimplyBot | "+event.getAuthor().getAsTag()).info("["+event.getGuild().getName()+"|"+event.getGuild().getId()+"] "+event.getMessage().getContentDisplay());
            }
        });
    }

}
