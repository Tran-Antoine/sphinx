package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.UUIDHolder;
import net.starype.quiz.api.game.question.Question;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class IndividualRound implements GameRound {

    private double maxToAward;
    private OneTryDistribution scoreDistribution;
    private Question pickedQuestion;
    private MaxGuessCounter maxGuessCounter;
    private Collection<? extends UUIDHolder> players;

    public IndividualRound(double maxToAward, Question pickedQuestion) {
        this.maxToAward = maxToAward;
        this.pickedQuestion = pickedQuestion;
    }

    @Override
    public void start(QuizGame game, Collection<? extends UUIDHolder> players, EventHandler eventHandler) {
        this.scoreDistribution = new OneTryDistribution(maxToAward);
        this.players = players;
        this.maxGuessCounter = new MaxGuessCounter(1);
        game.sendInputToServer((server) -> server.onQuestionReleased(pickedQuestion));
    }

    @Override
    public PlayerGuessContext onGuessReceived(UUIDHolder source, String message) {
        Optional<Double> optAccuracy = pickedQuestion.evaluateAnswer(Answer.fromString(message));
        if(optAccuracy.isEmpty()) {
            return new PlayerGuessContext(source, 0, true);
        }
        double accuracy = optAccuracy.get();
        scoreDistribution.addIfNew(source, accuracy);
        maxGuessCounter.incrementGuess(source);
        return new PlayerGuessContext(source, accuracy, false);
    }

    @Override
    public void onGiveUpReceived(UUIDHolder source) {
        scoreDistribution.addIfNew(source, 0);
    }

    @Override
    public EntityEligibility initPlayerEligibility() {
        return maxGuessCounter;
    }

    @Override
    public RoundEndingPredicate initEndingCondition() {
        return new NoGuessLeft(maxGuessCounter, players);
    }

    @Override
    public ScoreDistribution initScoreDistribution() {
        return new OneTryDistribution(maxToAward);
    }

    @Override
    public GameRoundReport initReport() {
        return Collections::emptyList;
    }

    @Override
    public GameRoundContext getContext() {
        return new GameRoundContext(this);
    }
}
