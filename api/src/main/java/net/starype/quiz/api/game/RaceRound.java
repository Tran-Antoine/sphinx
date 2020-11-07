package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RaceRound implements GameRound {

    private Question pickedQuestion;
    private AtomicReference<UUIDHolder> winnerContainer;
    private Collection<? extends UUIDHolder> players;
    private QuizGame game;
    private GameRoundContext context;
    private double pointsToAward;

    private MaxGuessCounter counter;

    public RaceRound(int maxGuessesPerPlayer, Question pickedQuestion, double pointsToAward) {
        this.counter = new MaxGuessCounter(maxGuessesPerPlayer);
        this.pickedQuestion = pickedQuestion;
        this.pointsToAward = pointsToAward;
    }

    @Override
    public void init(QuizGame game, Collection<? extends UUIDHolder> players) {
        this.winnerContainer = new AtomicReference<>();
        this.players = players;
        this.context = new GameRoundContext(this);
        this.game = game;
    }

    @Override
    public void onGuessReceived(UUIDHolder source, String message) {

        // eligibility checks are performed in the game class
        double pointsAwarded = pickedQuestion.submitAnswer(message);

        if(1 - pointsAwarded > 0.01) {
            counter.wrongGuess(source);
        } else {
            winnerContainer.set(source);
        }

        PlayerGuessContext context = new PlayerGuessContext(source, pointsAwarded, counter.isEligible(source));
        if(game != null) {
            game.sendInputToServer((server) -> server.onPlayerGuessed(context));
        }
    }

    @Override
    public void onGiveUpReceived(UUIDHolder source) {
        counter.giveUp(source);
    }

    @Override
    public EntityEligibility initPlayerEligibility() {
        return counter;
    }

    @Override
    public RoundEndingPredicate initEndingCondition() {
        return new WinnerExists(winnerContainer).or(new NoGuessLeft(counter, players));
    }

    @Override
    public ScoreDistribution initScoreDistribution() {
        return new SingleWinnerDistribution(winnerContainer, pointsToAward);
    }

    @Override
    public GameRoundReport initReport() {
        return () -> winnerContainer.get() == null
                ? winnerlessReport()
                : winnerReport();
    }

    private List<String> winnerReport() {
        return Arrays.asList(
                winnerContainer.get().getUUID().toString(),
                Double.toString(pointsToAward));
    }

    private List<String> winnerlessReport() {
        return Collections.singletonList("No winner for this round");
    }

    @Override
    public GameRoundContext getContext() {
        return context;
    }

    public static class Builder {

        private int maxGuessesPerPlayer;
        private Question pickedQuestion;
        private double pointsToAward;

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

        public RaceRound build() {
            return new RaceRound(maxGuessesPerPlayer, pickedQuestion, pointsToAward);
        }
    }
}
