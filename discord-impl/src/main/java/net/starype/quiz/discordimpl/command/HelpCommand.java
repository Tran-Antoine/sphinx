package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Collection;

public class HelpCommand implements QuizCommand {

    private Collection<? extends DisplayableCommand> commands;

    public HelpCommand(Collection<? extends DisplayableCommand> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(CommandContext context) {
        context.getChannel().sendMessage(createMessage()).queue(null, null);
    }

    private MessageEmbed createMessage() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        builder.setTitle("List of available commands");

        for (DisplayableCommand command : commands) {
            String description = command.getDescription();
            String nonNullDescription = description == null
                    ? "No description given for this command"
                    : description;
            builder.addField(command.getName(), nonNullDescription, false);
        }
        return builder.build();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Display the list of all commands";
    }
}
