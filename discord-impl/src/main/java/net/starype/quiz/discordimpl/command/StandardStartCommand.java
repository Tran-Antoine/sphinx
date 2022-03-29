package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.starype.quiz.discordimpl.game.GameLobby;

public class StandardStartCommand extends StartCommand {

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
