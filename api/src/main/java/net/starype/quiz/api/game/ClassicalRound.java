package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.UpdatableHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.*;

public class ClassicalRound implements GameRound {

    private Question pickedQuestion;
    private Collection<? extends IDHolder<?>> players;
    private MaxGuessCounter counter;
    private double maxAwarded;
    private LeaderboardDistribution leaderboard;

    public ClassicalRound(Question pickedQuestion, int maxGuesses, double maxAwarded) {
        this.pickedQuestion = pickedQuestion;
        this.counter = new MaxGuessCounter(maxGuesses);
        this.maxAwarded = maxAwarded;
    }

    @Override
    public void start(QuizGame game, Collection<? extends IDHolder<?>> players, UpdatableHandler updatableHandler) {
        this.players = players;
        this.leaderboard = new LeaderboardDistribution(maxAwarded, players.size());
        game.sendInputToServer(server -> server.onQuestionReleased(pickedQuestion));
    }

    @Override
    public PlayerGuessContext onGuessReceived(Player<?> source, String message) {

        Optional<Double> optCorrectness = pickedQuestion.evaluateAnswer(Answer.fromString(message));
        if(optCorrectness.isEmpty()) {
            return new PlayerGuessContext(source, 0, true);
        }

        double correctness = optCorrectness.get();

        counter.incrementGuess(source);
        leaderboard.scoreUpdate(source, correctness);

        if(Math.abs(correctness - 1) < ScoreDistribution.EPSILON) {
            counter.consumeAllGuesses(source);
        }

        return new PlayerGuessContext(source, correctness, counter.isEligible(source));
    }

    @Override
    public void onGiveUpReceived(IDHolder<?> source) {
        counter.consumeAllGuesses(source);
    }

    @Override
    public EntityEligibility initPlayerEligibility() {
        return counter;
    }

    @Override
    public RoundEndingPredicate initEndingCondition() {
        return new NoGuessLeft(counter, players).or(new FixedLeaderboardEnding(leaderboard, players.size()));
    }

    @Override
    public ScoreDistribution initScoreDistribution() {
        return leaderboard;
    }

    @Override
    public GameRoundReport initReport(List<Standing> standings) {
        return new SimpleGameReport(standings);
    }

    @Override
    public GameRoundContext getContext() {
        return new GameRoundContext(this);
    }
}
