package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.question.Question;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a round that is not competitive, whose purpose simply is to create statistics from the answers
 * provided by the players. Players still have a limit as to how many guesses they may take. Once a player
 * is satisfied with their answer, they might lock it by giving up on their remaining guesses.
 */
public class PollRound implements GameRound {

    private Question question;
    private final int maxGuesses;
    private Map<IDHolder, Answer> definitiveAnswers;
    private MaxGuessCounter counter;
    private Collection<? extends IDHolder> players;

    public PollRound(Question question, int maxGuesses) {
        this.question = question;
        this.maxGuesses = maxGuesses;
        this.definitiveAnswers = new HashMap<>();
    }

    @Override
    public void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler) {
        this.counter = new MaxGuessCounter(maxGuesses);
        this.players = players;
        game.sendInputToServer((server) -> server.onQuestionReleased(question));
    }

    @Override
    public PlayerGuessContext onGuessReceived(IDHolder<?> source, String message) {
        definitiveAnswers.put(source, Answer.fromString(message));
        counter.incrementGuess(source);
        return new PlayerGuessContext(source, 0, counter.isEligible(source));
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
        return new NoGuessLeft(counter, players);
    }

    @Override
    public ScoreDistribution initScoreDistribution() {
        return new ZeroScoreDistribution();
    }

    @Override
    public GameRoundReport initReport() {
        return () -> definitiveAnswers
                .entrySet()
                .stream()
                .map((entry) -> entry.getKey().getId()+": "+entry.getValue().getAnswerText())
                .collect(Collectors.toList());
    }

    @Override
    public GameRoundContext getContext() {
        return new GameRoundContext(this);
    }
}
