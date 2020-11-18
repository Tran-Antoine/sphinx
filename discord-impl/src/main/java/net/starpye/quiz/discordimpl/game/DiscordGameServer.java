package net.starpye.quiz.discordimpl.game;

import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.server.GameServer;

public class DiscordGameServer implements GameServer {
    @Override
    public void onRoundEnded(GameRoundReport report, QuizGame game) {

    }

    @Override
    public void onGameOver() {

    }

    @Override
    public void onPlayerGuessed(PlayerGuessContext context) {

    }

    @Override
    public void onNonEligiblePlayerGuessed(IDHolder player) {

    }

    @Override
    public void onPlayerGaveUp(IDHolder player) {

    }

    @Override
    public void onPlayerScoreUpdated(Player player) {

    }

    @Override
    public void onQuestionReleased(Question question) {

    }
}
