package ru.simplykel.simplybot.commands.information;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.simplykel.simplybot.Colors;

public class Help {
    public static EmbedBuilder embed = new EmbedBuilder()
            .setTitle("Сообщение помощи")
            .setColor(new Colors().OLD_LAVENDER)
            .addField("🧲 Информация",
                    "`help`, `change-log`, `ping`", false)
            .addField("<:FriendsIcon:920297662954221620> Minecraft",
                    "`server`, `skin`, `cape`", false)
            .addField("🧱 Утилиты",
                    "`user`, `avatar`", false)
            .addField("<:modrinth:993559544179478660> Modrinth",
                    "`project`, `search`", false)
            .addField("🎈 Веселья / игры",
                    "`ben`, `me`", false)
            .addField("RevolutionWorlds",
                    "`rw:player`, `rw:online`", false);
    public Help(MessageReceivedEvent event){

        event.getMessage().replyEmbeds(embed.build()).mentionRepliedUser(false).queue();
    }
    public Help(SlashCommandInteractionEvent event){
        event.replyEmbeds(embed.build()).mentionRepliedUser(false).queue();
    }
}
