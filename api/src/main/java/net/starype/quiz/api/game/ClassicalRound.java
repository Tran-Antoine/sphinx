package net.starype.quiz.api.game;

import net.starype.quiz.api.LeaderboardDistribution;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.UUIDHolder;
import net.starype.quiz.api.game.question.Question;

import java.util.Collection;
import java.util.Optional;

public class ClassicalRound implements GameRound {

    private Question pickedQuestion;
    private Collection<? extends UUIDHolder> players;
    private MaxGuessCounter counter;
    private double maxAwarded;

    public ClassicalRound(Question pickedQuestion, int maxGuesses, double maxAwarded) {
        this.pickedQuestion = pickedQuestion;
        this.counter = new MaxGuessCounter(maxGuesses);
        this.maxAwarded = maxAwarded;
    }

    @Override
    public void start(QuizGame game, Collection<? extends UUIDHolder> players, EventHandler eventHandler) {
        this.players = players;
    }

    @Override
    public void onGuessReceived(UUIDHolder source, String message) {

        Optional<Double> score = pickedQuestion.evaluateAnswer(Answer.fromString(message));

    }

    @Override
    public void onGiveUpReceived(UUIDHolder source) {
        counter.giveUp(source);
    }

    @Override
    public void onRoundStopped() {
    }

    @Override
    public EntityEligibility initPlayerEligibility() {
        return counter;
    }

    @Override
    public RoundEndingPredicate initEndingCondition() {
        return new NoGuessLeft(counter, players);
    }

    @Override
    public ScoreDistribution initScoreDistribution() {
        return new LeaderboardDistribution(maxAwarded);
    }

    @Override
    public GameRoundReport initReport() {
        return null;
    }

    @Override
    public GameRoundContext getContext() {
        return new GameRoundContext(this);
    }
}
