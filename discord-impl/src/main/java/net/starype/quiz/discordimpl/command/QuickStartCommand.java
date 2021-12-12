package net.starype.quiz.discordimpl.command;

import net.starype.quiz.api.round.IndividualRoundFactory;
import net.starype.quiz.discordimpl.game.GameLobby;

public class QuickStartCommand extends StartCommand {

    @Override
    public String getName() {
        return "quickstart";
    }

    @Override
    public String getDescription() {
        return "Start the game with default parameters";
    }

    @Override
    protected void onPreStart(GameLobby lobby) {
        lobby.resetRounds();
        RoundAddCommand.PartialRound defaultRound = q -> new IndividualRoundFactory().create(q, 1);
        lobby.queueRound(defaultRound, 5);
    }
}
