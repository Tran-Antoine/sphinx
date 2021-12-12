package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class ProcedureDisplayCommand implements QuizCommand {

    private static final String PATH = "discord-impl/src/main/resources/rules.md";

    @Override
    public void execute(CommandContext context) {
        String content = readFromFile();
        String[] fields = content.split(Pattern.quote("<skip>"));

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.CYAN);
        builder.setTitle(fields[0]);

        for(int i = 1; i < fields.length; i++) {
            String field = fields[i];
            builder.addBlankField(false);
            builder.addField(":small_orange_diamond: Step " + i, field, false);
        }
        context.getChannel().sendMessageEmbeds(builder.build()).queue(null, null);
    }

    private String readFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(PATH));
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            reader.close();
            return builder.toString();
        } catch (IOException e) {
            return "Rules not found :(";
        }
    }

    @Override
    public String getName() {
        return "how-to-play";
    }

    @Override
    public String getDescription() {
        return "Read the rules of the game!";
    }
}
