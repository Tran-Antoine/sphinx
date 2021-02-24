package helloquiz;

import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.player.Player;
import net.starype.quiz.api.question.Question;
import net.starype.quiz.api.server.GameServer;

import java.util.List;

public class HelloServer implements GameServer<QuizGame> {

    private boolean gameOver = false;


    @Override
    public void onRoundStarting(QuizGame game, boolean firstRound) {
        if(firstRound) {
            System.out.println("Welcome to the first round of the game!");
        } else {
            System.out.println("A new round is about to start!");
        }
    }

    @Override
    public void onRoundEnded(GameRoundReport report, QuizGame game) {
        System.out.println("Round ended. Results:");
        for(Standing standing : report.orderedStandings()) {
            System.out.println(standing.getPlayer().getNickname()+": " + standing.getScoreAcquired());
        }
        System.out.println();

        game.nextRound();
    }

    @Override
    public void onGameOver(List<? extends Player<?>> standings, QuizGame game) {
        System.out.println("All rounds have been played. Game successfully stopped");
        gameOver = true;
    }

    @Override
    public void onPlayerGuessed(PlayerGuessContext context) {
        System.out.println("Player with ID " + context.getPlayer().getId() + " sent a guess");
        double pointsAwarded = context.getCorrectness();
        System.out.println("Score Accuracy: " + pointsAwarded);
        System.out.println("Player might try again: " + context.isEligible());
        System.out.println();
    }

    @Override
    public void onNonEligiblePlayerGuessed(Player<?> player) {
        System.out.println("Input refused. Player with ID " + player.getId() + " may not send a guess");
    }

    @Override
    public void onPlayerGaveUp(Player<?> player) {
        System.out.println("Player with ID " + player.getId() + " gave up on the round");
    }

    @Override
    public void onPlayerScoreUpdated(Player<?> player) {
        System.out.println("Score update for player with ID " + player.getId() +": " + player.getScore().getPoints());
    }

    @Override
    public void onQuestionReleased(Question question) {
        System.out.println(question.getRawQuestion());
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
