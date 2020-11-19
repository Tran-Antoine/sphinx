package net.starpye.quiz.discordimpl.command;

import net.starpye.quiz.discordimpl.game.GameLobby;
import net.starpye.quiz.discordimpl.game.LobbyList;

import java.util.Optional;

public class StartGameCommand implements DiscordCommand {

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Starts a game. Must follow /create";
    }

    @Override
    public void execute(CommandContext context) {
        LobbyList lobbyList = context.getLobbyList();
        Optional<GameLobby> optGame = lobbyList.findByAuthor(context.getAuthor().getId());
        if(optGame.isEmpty()) {
            String nickName = context.getAuthor().getDisplayName();
            context.getChannel().createMessage(nickName +", you are not the admin of any lobby").block();
            return;
        }
        GameLobby gameLobby = optGame.get();
        lobbyList.unregisterLobby(gameLobby);
        gameLobby.start(context.getGameList());
    }
}
