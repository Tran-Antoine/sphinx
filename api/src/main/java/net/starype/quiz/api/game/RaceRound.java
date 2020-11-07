package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public class RaceRound implements GameRound {

    private Question pickedQuestion;
    private AtomicReference<UUIDHolder> winnerContainer;
    private Collection<? extends UUIDHolder> players;
    private double pointsToAward;

    private MaxGuessCounter counter;

    public RaceRound(int maxGuessesPerPlayer, Question pickedQuestion, double pointsToAward,
                     Collection<? extends UUIDHolder> players) {
        this.counter = new MaxGuessCounter(maxGuessesPerPlayer);
        this.pickedQuestion = pickedQuestion;
        this.pointsToAward = pointsToAward;
        this.players = players;
    }

    @Override
    public void init() {
        this.winnerContainer = new AtomicReference<>();
    }

    @Override
    public void onGuessReceived(UUIDHolder source, String message) {

        // eligibility checks are performed in the game class
        if(pickedQuestion.submitAnswer(message) != 1) {
            counter.wrongGuess(source);
            return;
        }

        winnerContainer.set(source);
    }

    @Override
    public void onGiveUpReceived(UUIDHolder source) {
        counter.giveUp(source);
    }

    @Override
    public AnswerEligibility playerEligibility() {
        return counter;
    }

    @Override
    public RoundEndingPredicate endingCondition() {
        return new WinnerExists(winnerContainer).or(new NoGuessLeft(counter, players));
    }

    @Override
    public ScoreDistribution createScoreDistribution() {
        return new SingleWinnerDistribution(winnerContainer, pointsToAward);
    }

    @Override
    public GameRoundReport createReport() {
        return () -> Arrays.asList(
                winnerContainer.get().getUUID().toString(),
                Double.toString(pointsToAward));
    }

    public static class Builder {

        private int maxGuessesPerPlayer;
        private Question pickedQuestion;
        private double pointsToAward;
        private Collection<? extends UUIDHolder> players;

        public Builder withMaxGuessesPerPlayer(int maxGuessesPerPlayer) {
            this.maxGuessesPerPlayer = maxGuessesPerPlayer;
            return this;
        }

        public Builder withQuestion(Question pickedQuestion) {
            this.pickedQuestion = pickedQuestion;
            return this;
        }

        public Builder withPointsToAward(double pointsToAward) {
            this.pointsToAward = pointsToAward;
            return this;
        }

        public Builder withPlayers(Collection<? extends UUIDHolder> players) {
            this.players = players;
            return this;
        }

        public RaceRound build() {
            return new RaceRound(maxGuessesPerPlayer, pickedQuestion, pointsToAward, players);
        }
    }
}
