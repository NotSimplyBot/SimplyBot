package ru.simplykel.simplybot.commands.games;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import ru.simplykel.simplybot.Colors;
import ru.simplykel.simplybot.Main;

import java.io.File;

public class Ben {
    private File image;
    private EmbedBuilder help = new EmbedBuilder().setTitle("Говорящий Бен")
            .setDescription("Задайте вопрос Бену, и он возможно вам ответит :)")
            .addField("Пример:","```\n"+Main.settings.prefix+"ben я гей?\n```", false);
    public Ben(MessageReceivedEvent event, String[] args){
        if(args.length < 0){
            event.getMessage().replyEmbeds(help.build()).mentionRepliedUser(false).queue();
            return;
        };
        String amogus = event.getMessage().getContentDisplay().replace(Main.settings.prefix+"ben ", "").replace(Main.settings.prefix+"бен ", "");

        event.getMessage().replyEmbeds(execute(embed(amogus)).build())
                .mentionRepliedUser(false)
                .addFiles(FileUpload.fromData(image, "ben.gif"))
                .queue();
    }
    public Ben(SlashCommandInteractionEvent event){
        if(event.getOption("request") == null){
            event.replyEmbeds(help.build()).mentionRepliedUser(false).queue();
            return;
        };
        event.deferReply().queue(response -> {
            String amogus = event.getOption("request").getAsString();

            response.editOriginalEmbeds(execute(embed(amogus)).build())
                    .setFiles(FileUpload.fromData(image, "ben.gif"))
                    .queue();
        });

    }
    public EmbedBuilder execute(EmbedBuilder embedBuilder){
        double Random = Math.random();
        if(Random <= 0.25){
            image = new File(Main.settings.folder+"assets/ben/ho-ho-ho.gif");
            embedBuilder.addField("Ответ Бена", "> ho-ho-ho", false);
        } else if(Random <= 0.65){
            image = new File(Main.settings.folder+"assets/ben/no.gif");
            embedBuilder.addField("Ответ Бена", "> No", false);
        } else if(Random <= 0.75){
            image = new File(Main.settings.folder+"assets/ben/yes.gif");
            embedBuilder.addField("Ответ Бена", "> Yes", false);
        } else {
            image = new File(Main.settings.folder+"assets/ben/eleble.gif");
            embedBuilder.addField("Ответ Бена", "> *не разборчиво*", false);
        }
        return embedBuilder;
    }
    public EmbedBuilder embed(String request){
        EmbedBuilder response = new EmbedBuilder();
        response.setColor(new Colors().GREEN);
        response.setTitle("Говорящий Бен");
        response.setDescription("> "+request);
        response.setImage("attachment://ben.gif");
        return response;
    }

}
