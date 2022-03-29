package net.starype.quiz.discordimpl.command;

import net.starype.quiz.discordimpl.game.GameLobby;

public class StartStandardCommand extends StartCommand {

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Start a game. Must follow /create";
    }

    @Override
    protected void onPreStart(GameLobby lobby) {

    }
}
