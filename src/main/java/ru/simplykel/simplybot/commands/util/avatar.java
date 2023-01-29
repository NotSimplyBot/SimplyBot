package ru.simplykel.simplybot.commands.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.simplykel.simplybot.Colors;

public class avatar {
    public User user;
    public Member member;
    public avatar(MessageReceivedEvent event){
        if(event.getMessage().getMentions().getMembers().size() > 0){
            user = event.getMessage().getMentions().getMembers().get(0).getUser();
            member = event.getMessage().getMentions().getMembers().get(0);
        } else {
            user = event.getAuthor();
            member = event.getMember();
        }
        event.getMessage().replyEmbeds(info().build()).mentionRepliedUser(false).queue();
    }
    public avatar(SlashCommandInteractionEvent event){
        if(event.getOption("user") != null){
            user = event.getOption("user").getAsMember().getUser();
            member = event.getOption("user").getAsMember();
        } else {
            user = event.getUser();
            member = event.getMember();
        }
        event.replyEmbeds(info().build()).queue();
    }
    public avatar(UserContextInteractionEvent event){
        user = event.getTarget();
        member = event.getTargetMember();
        event.replyEmbeds(info().build()).queue();
    }
    public EmbedBuilder info() {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Аватар пользователя "+user.getAsTag())
                .setColor(new Colors().OLD_LAVENDER)
                .setImage(getURL(member, user));
        return embed;
    }
    public static String getURL(Member reqMember, User reqUser){
        String url;
        if(reqMember.getAvatarUrl() == null){
            if(reqUser.getAvatarUrl() == null){
                url = reqUser.getDefaultAvatarUrl();
            } else {
                url = reqUser.getAvatarUrl();
            }
        } else {
            url = reqMember.getAvatarUrl();
        }
        url = url+"?size=480";
        return url;
    }
}
