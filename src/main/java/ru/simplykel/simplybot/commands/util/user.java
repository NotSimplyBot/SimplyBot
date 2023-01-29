package ru.simplykel.simplybot.commands.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class user {
    public User user;
    public Member member;
    public user(MessageReceivedEvent event){
        if(event.getMessage().getMentions().getMembers().size() > 0){
            user = event.getMessage().getMentions().getUsers().get(0);
            member = event.getMessage().getMentions().getMembers().get(0);
        } else {
            user = event.getAuthor();
            member = event.getMember();
        }
        event.getMessage().replyEmbeds(info().build()).mentionRepliedUser(false).queue();
    }
    public user(SlashCommandInteractionEvent event){
        if(event.getOption("user") != null){
            user = event.getOption("user").getAsUser();
            member = event.getOption("user").getAsMember();
        } else {
            user = event.getUser();
            member = event.getMember();
        }
        event.replyEmbeds(info().build()).queue();
    }
    public user(UserContextInteractionEvent event){
        user = event.getTarget();
        member = event.getTargetMember();
        event.replyEmbeds(info().build()).queue();
    }
    public EmbedBuilder info(){
        long joined = member.getTimeJoined().toEpochSecond();
        long register = member.getTimeCreated().toEpochSecond();
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Информация пользователя "+user.getAsTag())
                .setThumbnail(avatar.getURL(member, user))
                .setColor(new Color(0x32373B));
        /*
         * #32373B - Не в сети или невидимка
         * #88BB92 - В сети
         * #F4B860 - Не активен
         * #C83E4D - Не беспокоить
         */
        if(member.getOnlineStatus().equals(OnlineStatus.ONLINE)) embed.setColor(new Color(0x88BB92));
        else if(member.getOnlineStatus().equals(OnlineStatus.IDLE)) embed.setColor(new Color(0xF4B860));
        else if(member.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB)) embed.setColor(new Color(0xC83E4D));
        StringBuilder description = new StringBuilder();
        description.append("Зарегистрирован в <t:").append(register).append(":f>").append("\n");
        description.append("Зашёл на данный сервер в <t:").append(joined).append(":f>").append("\n");
        if(member.isOwner()) description.append("Пользователь является владельцем данного сервера").append("\n");
        else if(user.isBot()) description.append("Пользователь является ботом").append("\n");
        else if(user.isSystem()) description.append("Пользовать? не, это Система. Сама в шоке :)").append("\n");
        else if(member.isBoosting()) description.append("Пользователь забустил сервер`[возможно]`").append("\n");
        if(!member.getActivities().isEmpty()){
            for(int i=0;i<member.getActivities().size();i++){
                StringBuilder appInfo = new StringBuilder();
                Activity activity = member.getActivities().get(i);
                if(activity.getType().equals(Activity.ActivityType.CUSTOM_STATUS)) description.append("Статус активности: ").append(activity.getName());
                else if(activity.getType().equals(Activity.ActivityType.LISTENING)) {
                    if(activity.getName().equals("Spotify")){
                        embed.addField("Spotify", activity.asRichPresence().getDetails() +"**\n> by **"+ activity.asRichPresence().getState()+"**\n" + "> on **"+activity.asRichPresence().getLargeImage().getText()+"**\n", false);
                    }
                } else if(activity.getType().equals(Activity.ActivityType.PLAYING)) {
                    if(activity.asRichPresence() == null) appInfo.append("Начал играть ").append("<t:"+(System.currentTimeMillis()-activity.getTimestamps().getStart())+":f>");
                    else {
                        if (activity.asRichPresence().getDetails() == null && activity.asRichPresence().getState() != null) {
                            appInfo.append("> "+removeFormating(activity.asRichPresence().getState()));
                        } else if (activity.asRichPresence().getState() == null && activity.asRichPresence().getDetails() != null) {
                            appInfo.append("> "+removeFormating(activity.asRichPresence().getDetails()));
                        } else if (activity.asRichPresence().getDetails() == null && activity.asRichPresence().getState() == null) {
                            appInfo.append("> *Доп. информации нет*");
                        } else {
                            appInfo.append("> "+removeFormating(activity.asRichPresence().getDetails()) + " \n> " + removeFormating(activity.asRichPresence().getState()));
                        }
                        if(activity.asRichPresence().getTimestamps() != null){
//                            long time = (msg.getTimeCreated().toEpochSecond() - activity.asRichPresence().getTimestamps().getStart());

                            // appInfo.append("\n> :clock4: " + formatTime(time));
                        }
                    }
                    embed.addField(activity.getName(), appInfo.toString(), false);
                }
            }
        }
        embed.setDescription(description.toString());
        return embed;
    }
    public static String removeFormating(String text){
        String res = text.replace("*","\\*")
                .replace("_","\\_")
                .replace("~","\\~")
                .replace("`","\\`")
                .replace("|","\\|")
                .replace(">","\\>")
                .replace("\\n"," ");
        return res;
    }
}
