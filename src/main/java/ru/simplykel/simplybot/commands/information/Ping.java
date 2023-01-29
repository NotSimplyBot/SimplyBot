package ru.simplykel.simplybot.commands.information;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.simplykel.simplybot.Colors;
import ru.simplykel.simplybot.Main;

public class Ping {
    EmbedBuilder builder = new EmbedBuilder().setColor(new Colors().GREEN)
            .setDescription("⚡ Мой пинг между сервисом discord.com: **`"+ Main.bot.getGatewayPing() +"`**");
    public Ping(MessageReceivedEvent event){
        event.getMessage().replyEmbeds(builder.build()).mentionRepliedUser(false).queue();
    }
    public Ping(SlashCommandInteractionEvent event){
        event.replyEmbeds(builder.build()).queue();
    }
}
