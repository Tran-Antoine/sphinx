package net.starpye.quiz.discordimpl.command;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RulesDisplayCommand implements QuizCommand {

    private static final String PATH = "discord-impl/src/main/resources/rules.md";

    @Override
    public void execute(CommandContext context) {
        String content = readFromFile();
        context.getChannel().createMessage(content).subscribe();
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
        return "rules";
    }

    @Override
    public String getDescription() {
        return "Read the rules of the game!";
    }
}
