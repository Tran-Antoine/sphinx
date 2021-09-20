package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface DisplayableCommand {

    String getName();
    String getDescription();

    CommandData getData();

    default CommandData dataTemplate() {
        return new CommandData(getName(), getDescription());
    }
}
