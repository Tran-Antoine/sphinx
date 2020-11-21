package net.starype.quiz.api.server;

import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.List;

public interface GameServer<T extends QuizGame> {

    void onRoundEnded(GameRoundReport report, T game);
    void onGameOver(T game, List<? extends Player<?>> playerStandings);
    void onPlayerGuessed(PlayerGuessContext context);
    void onNonEligiblePlayerGuessed(Player<?> player);
    void onPlayerGaveUp(Player<?> player);
    void onPlayerScoreUpdated(Player<?> player);
    void onQuestionReleased(Question question);
}
