package net.starpye.quiz.discordimpl.command;

import net.starpye.quiz.discordimpl.game.GameLobby;

public class CreateGameCommand implements DiscordCommand {

    @Override
    public void execute(CommandContext context) {
        GameLobby lobby = new GameLobby();
        lobby.registerPlayer(context.getAuthorId());
        lobby.openToPublic();
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Sets up a game lobby that can be started at the author's request";
    }
}
