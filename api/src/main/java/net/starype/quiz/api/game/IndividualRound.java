package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class IndividualRound implements GameRound {

    private final double maxToAward;
    private final Question pickedQuestion;
    private final Map<IDHolder<?>, ModifiablePlayerReport> playerReports;
    private OneTryDistribution scoreDistribution;
    private MaxGuessCounter maxGuessCounter;
    private Collection<? extends IDHolder<?>> players;

    public IndividualRound(Question pickedQuestion, double maxToAward) {
        this.maxToAward = maxToAward;
        this.pickedQuestion = pickedQuestion;
        playerReports = new HashMap<>();
    }

    @Override
    public void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler) {
        this.scoreDistribution = new OneTryDistribution(maxToAward);
        this.players = players;
        players.forEach(player -> playerReports.put(player, new ModifiablePlayerReport(player)));
        this.maxGuessCounter = new MaxGuessCounter(1);
        game.sendInputToServer((server) -> server.onQuestionReleased(pickedQuestion));
    }

    @Override
    public PlayerGuessContext onGuessReceived(Player<?> source, String message) {
        playerReports.get(source).registerSolution(message);
        Optional<Double> optAccuracy = pickedQuestion.evaluateAnswer(Answer.fromString(message));
        if(optAccuracy.isEmpty()) {
            return new PlayerGuessContext(source, 0, true);
        }
        double accuracy = optAccuracy.get();

        playerReports.get(source).setReward(accuracy);
        scoreDistribution.addIfNew(source, accuracy);
        maxGuessCounter.incrementGuess(source);

        return new PlayerGuessContext(source, accuracy, false);
    }

    @Override
    public void onGiveUpReceived(IDHolder<?> source) {
        scoreDistribution.addIfNew(source, 0);
        maxGuessCounter.consumeAllGuesses(source);
        playerReports.get(source).giveUp();
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
        return scoreDistribution;
    }

    @Override
    public GameRoundReport initReport(List<Standing> standings) {
        return new SimpleGameReport(standings, pickedQuestion.getDisplayableCorrectAnswer(), playerReports.values());
    }

    @Override
    public GameRoundContext getContext() {
        return new GameRoundContext(this);
    }
}
