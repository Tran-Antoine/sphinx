package net.starype.quiz.api.game.mock;

import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.UUIDHolder;
import net.starype.quiz.api.server.GameServer;

public class MockServer implements GameServer {

    private boolean gameOver = false;

    @Override
    public void onRoundEnded(GameRoundReport report, QuizGame game) {
        System.out.println("Round ended. Results:");
        System.out.println(report.rawMessages());
        System.out.println();
        game.nextRound();
    }

    @Override
    public void onGameOver() {
        System.out.println("All rounds have been played. Game successfully stopped");
        gameOver = true;
    }

    @Override
    public void onPlayerGuessed(PlayerGuessContext context) {
        System.out.println("Player with ID " + context.getPlayer().getUUID() + " sent a guess");
        double pointsAwarded = context.getCorrectness();
        System.out.println("Score Accuracy: " + pointsAwarded);
        System.out.println("Player might try again: " + context.isEligible());
        System.out.println();
    }

    @Override
    public void onNonEligiblePlayerGuessed(UUIDHolder player) {
        System.out.println("Input refused. Player with ID " + player.getUUID() + " may not send a guess");
    }

    @Override
    public void onPlayerGaveUp(UUIDHolder player) {
        System.out.println("Player with ID " + player.getUUID() + " gave up on the question");
    }

    @Override
    public void onPlayerScoreUpdated(Player player) {
        System.out.println("Score update for player with ID " + player.getUUID() +": " + player.getScore().getPoints());
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
