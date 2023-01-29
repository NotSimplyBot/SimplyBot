package ru.simplykel.simplybot.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
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
import ru.simplykel.simplybot.commands.modrinth.search;
import ru.simplykel.simplybot.commands.util.avatar;
import ru.simplykel.simplybot.commands.util.user;

public class Interaction extends ListenerAdapter {
    public Interaction(JDA bot){
        bot.updateCommands().addCommands(
                Commands.slash("help", "Cообщение помощи"),
                Commands.slash("ping", "Мой пинг"),
                Commands.slash("rw-online", "Онлайн сервера"),
                Commands.slash("rw-player", "Информация игрока")
                        .addOption(OptionType.STRING, "player", "Игрок", false),
                Commands.slash("profile", "Профиль пользователя")
                        .addOption(OptionType.USER, "user", "Пользователь", false),
                Commands.context(Command.Type.USER, "Профиль"),
                Commands.slash("avatar", "Аватар пользователя")
                        .addOption(OptionType.USER, "user", "Пользователь", false),
                Commands.context(Command.Type.USER, "Аватар"),
                Commands.slash("ben", "Cпроси у Бена что-нибудь :/")
                        .addOption(OptionType.STRING, "request", "Ваш запрос", false),
                Commands.slash("server", "Информация Minecraft сервера")
                        .addOption(OptionType.STRING, "address", "Адрес сервера", true)
                        .addOption(OptionType.INTEGER, "protocol", "Протокол сервера", false),
                Commands.slash("project", "Информация о проекте из Modrinth")
                        .addOption(OptionType.STRING, "id", "ID или короткое название проекта", true),
                Commands.slash("search", "Поиск проектов на Modrinth")
                        .addOption(OptionType.STRING, "query", "Ваш запрос", true),
                Commands.slash("change_log", "Список изменений")
                        .addOption(OptionType.STRING, "version", "Версия", false),
                Commands.slash("me", "Сообщение действия")
                        .addOption(OptionType.STRING, "message", "действия", false),
                Commands.slash("skin", "Скин игрока Minecraft")
                        .addOption(OptionType.STRING, "name", "Никнейм игрока", false)
                        .addOption(OptionType.STRING, "url", "Ссылка на файл скина", false)
                        .addOption(OptionType.INTEGER, "api", "ID API [Не совместим с ссылкой]", false)
                        .addOption(OptionType.BOOLEAN, "slim", "Тип скина Алекс", false)
                        .addOption(OptionType.BOOLEAN, "head", "Покажет только голову", false),
                Commands.slash("cape", "Плащ игрока Minecraft")
                        .addOption(OptionType.STRING, "name", "Никнейм игрока", false)
                        .addOption(OptionType.INTEGER, "api", "ID API", false)
        ).queue();
//        bot.updateCommands().addCommands().queue();
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        String command = event.getName();
        switch (command){
            case "help" -> new Help(event);
            case "profile" -> new user(event);
            case "avatar" -> new avatar(event);
            case "ben" -> new Ben(event);
            case "ping" -> new Ping(event);
            case "server" -> new Server(event);
            case "project" -> new project(event);
            case "search" -> new search(event);
            case "change_log" -> new Change_log(event);
            case "me" -> new Me(event);
            case "skin" -> new Skins(event);
            case "cape" -> new Capes(event);
            case "rw-player" -> new player(event);
            case "rw-online" -> new online(event);
        }
    }
    @Override
    public void onUserContextInteraction(UserContextInteractionEvent event){
        String command = event.getName();
        switch (command) {
            case "Профиль" -> new user(event);
            case "Аватар" -> new avatar(event);
        }
    }
    @Override
    public void onMessageContextInteraction(MessageContextInteractionEvent event){
        String command = event.getName();
        switch (command){
        }
    }
}
