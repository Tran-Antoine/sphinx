package net.starpye.quiz.discordimpl.command;

public interface QuizCommand {

    void execute(CommandContext context);

    String getName();
    String getDescription();
}
