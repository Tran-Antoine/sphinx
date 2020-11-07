package net.starype.quiz.api.server;

import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.UUIDHolder;

public interface GameServer {

    void onRoundEnded(GameRoundReport report, QuizGame game);
    void onGameOver();
    void onPlayerGuessed(PlayerGuessContext context);
    void onPlayerGaveUp(UUIDHolder player);
    void onPlayerScoreUpdated(Player player);
}
