package net.starpye.quiz.discordimpl.command;

public interface DiscordCommand {

    String getName();
    String getDescription();

    void execute(CommandContext context);
}
