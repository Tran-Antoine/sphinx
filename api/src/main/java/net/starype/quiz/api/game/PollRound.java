package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a round that is not competitive, whose purpose simply is to create statistics from the answers
 * provided by the players. Players still have a limit as to how many guesses they may take. Once a player
 * is satisfied with their answer, they might lock it by giving up on their remaining guesses.
 */
public class PollRound implements GameRound {

    private final Question question;
    private final int maxGuesses;
    private final Map<IDHolder<?>, ModifiablePlayerReport> playerReports;
    private MaxGuessCounter counter;
    private Collection<? extends IDHolder<?>> players;

    public PollRound(Question question, int maxGuesses) {
        this.question = question;
        this.maxGuesses = maxGuesses;
        playerReports = new HashMap<>();
    }

    @Override
    public void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler) {
        this.counter = new MaxGuessCounter(maxGuesses);
        this.players = players;
        players.forEach(player -> playerReports.put(player, new ModifiablePlayerReport(player)));
        game.sendInputToServer((server) -> server.onQuestionReleased(question));
    }

    @Override
    public PlayerGuessContext onGuessReceived(Player<?> source, String message) {
        playerReports.get(source).registerSolution(message);
        counter.incrementGuess(source);
        return new PlayerGuessContext(source, 0, counter.isEligible(source));
    }

    @Override
    public void onGiveUpReceived(IDHolder<?> source) {
        counter.consumeAllGuesses(source);
        playerReports.get(source).giveUp();
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
    public GameRoundReport initReport(List<Standing> standings) {
        return new SimpleGameReport(Collections.emptyList(), question.getDisplayableCorrectAnswer(), playerReports.values());
    }

    @Override
    public GameRoundContext getContext() {
        return new GameRoundContext(this);
    }
}
