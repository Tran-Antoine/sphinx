package net.starype.quiz.api.server;

import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.question.Question;

public interface GameServer {

    void onRoundEnded(GameRoundReport report, QuizGame game);
    void onGameOver();
    void onPlayerGuessed(PlayerGuessContext context);
    void onNonEligiblePlayerGuessed(IDHolder<?> player);
    void onPlayerGaveUp(IDHolder<?> player);
    void onPlayerScoreUpdated(Player<?> player);
    void onQuestionReleased(Question question);
}
