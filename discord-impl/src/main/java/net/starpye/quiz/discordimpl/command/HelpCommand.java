package net.starpye.quiz.discordimpl.command;

import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;

import java.awt.*;
import java.util.Collection;

public class HelpCommand implements QuizCommand {

    private Collection<? extends QuizCommand> commands;

    public HelpCommand(Collection<? extends QuizCommand> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(CommandContext context) {
        context.getChannel().createMessage(this::createMessage).subscribe();
    }

    private void createMessage(MessageCreateSpec spec) {
        spec.setEmbed(embedSpec -> {
            embedSpec.setColor(Color.GREEN);
            embedSpec.setTitle("List of available commands");
            for (QuizCommand command : commands) {
                String description = command.getDescription() == null
                        ? "No description given for this command"
                        : command.getDescription();
                String nonNullDescription = description;
                embedSpec.addField(command.getName(), nonNullDescription, false);
            }
        });
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
