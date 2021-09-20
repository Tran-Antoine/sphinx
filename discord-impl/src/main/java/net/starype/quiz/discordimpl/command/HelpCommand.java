package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class HelpCommand implements QuizCommand {

    private final Collection<? extends DisplayableCommand> commands;

    public HelpCommand(Collection<? extends DisplayableCommand> commands) {
        this.commands = List.copyOf(commands);
    }

    @Override
    public void execute(CommandContext context) {
        context.getChannel().sendMessageEmbeds(createMessage()).queue(null, null);
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

    @Override
    public CommandData getData() {
        return dataTemplate();
    }
}
