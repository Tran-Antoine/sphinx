package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;
import net.starype.quiz.api.util.ObjectContainer;

import java.util.Arrays;
import java.util.Collection;

public class RaceRound implements GameRound {

    private Question pickedQuestion;
    private ObjectContainer<UUIDHolder> winnerContainer;
    private Collection<? extends UUIDHolder> players;
    private double pointsToAward;

    private MaxGuessCounter counter;

    public RaceRound(int maxGuessesPerPlayer, Question pickedQuestion, double pointsToAward, Collection<? extends UUIDHolder> players) {
        this.counter = new MaxGuessCounter(maxGuessesPerPlayer);
        this.pickedQuestion = pickedQuestion;
        this.pointsToAward = pointsToAward;
        this.players = players;
    }

    @Override
    public void init() {
        this.winnerContainer = ObjectContainer.emptyContainer();
    }

    @Override
    public void onInputReceived(UUIDHolder source, String message) {

        // eligibility checks are performed in the game class
        if(pickedQuestion.submitAnswer(message) != 1) {
            counter.wrongGuess(source);
            return;
        }

        winnerContainer.setObject(source);
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
}
