package net.starpye.quiz.discordimpl.command;

public interface QuizCommand extends DisplayableCommand {

    void execute(CommandContext context);

}
