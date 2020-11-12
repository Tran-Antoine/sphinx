package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.UUIDHolder;
import net.starype.quiz.api.game.question.Question;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class TimedRaceRound extends RaceRound {

    private TimeOutEnding timeOutEnding;

    public TimedRaceRound(int maxGuessesPerPlayer, Question pickedQuestion, double pointsToAward,
                          long time, TimeUnit unit) {
        super(maxGuessesPerPlayer, pickedQuestion, pointsToAward);
        this.timeOutEnding = new TimeOutEnding(time, unit);
    }

    @Override
    public void start(QuizGame game, Collection<? extends UUIDHolder> players, EventHandler eventHandler) {
        super.start(game, players, eventHandler);
        timeOutEnding.startTimer(() -> game.checkEndOfRound(this), eventHandler);
    }

    @Override
    public RoundEndingPredicate initEndingCondition() {
        return super.initEndingCondition().or(this.timeOutEnding);
    }

    @Override
    public void onRoundStopped() {
        timeOutEnding.shutDown();
    }

    public static class Builder {

        private int maxGuessesPerPlayer;
        private Question pickedQuestion;
        private double pointsToAward;
        private long time;
        private TimeUnit unit;

        public Builder withtMaxGuessesPerPlayer(int maxGuessesPerPlayer) {
            this.maxGuessesPerPlayer = maxGuessesPerPlayer;
            return this;
        }

        public Builder withPickedQuestion(Question pickedQuestion) {
            this.pickedQuestion = pickedQuestion;
            return this;
        }

        public Builder withPointsToAward(double pointsToAward) {
            this.pointsToAward = pointsToAward;
            return this;
        }

        public Builder withTimeUnit(TimeUnit unit) {
            this.unit = unit;
            return this;
        }

        public Builder withTime(long time) {
            this.time = time;
            return this;
        }

        public TimedRaceRound build() {
            return new TimedRaceRound(maxGuessesPerPlayer, pickedQuestion, pointsToAward, time, unit);
        }
    }
}
