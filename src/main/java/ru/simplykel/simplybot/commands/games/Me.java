package ru.simplykel.simplybot.commands.games;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.simplykel.simplybot.Colors;

public class Me {
    String you_gay = "пукнул";
    public Me(MessageReceivedEvent event, String[] args){
        if(args.length >= 2) you_gay=event.getMessage().getContentDisplay().replace(args[0]+" ", "");
        event.getChannel().sendMessageEmbeds(info(you_gay, event.getAuthor()).build()).queue();
    }
    public Me(SlashCommandInteractionEvent event){
        if(event.getOption("message") != null) you_gay=event.getOption("message").getAsString();
        event.replyEmbeds(info(you_gay, event.getUser()).build()).setEphemeral(true).queue();
        event.getChannel().sendMessageEmbeds(info(you_gay, event.getUser()).build()).queue();
    }
    public EmbedBuilder info(String youre_a_furry, User user){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Colors().OLD_LAVENDER)
                .setDescription(user.getAsMention()+": "+youre_a_furry);
        return builder;
    }
}
