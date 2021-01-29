package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.*;

public class ClassicalRound implements GameRound {

    private final Map<IDHolder<?>, ModifiablePlayerReport> playerReports;
    private final Question pickedQuestion;
    private final MaxGuessCounter counter;
    private final double maxAwarded;
    private Collection<? extends IDHolder<?>> players;
    private LeaderboardDistribution leaderboard;

    public ClassicalRound(Question pickedQuestion, int maxGuesses, double maxAwarded) {
        this.pickedQuestion = pickedQuestion;
        this.counter = new MaxGuessCounter(maxGuesses);
        this.maxAwarded = maxAwarded;
        this.playerReports = new HashMap<>();
    }

    @Override
    public void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler) {
        this.players = players;
        this.leaderboard = new LeaderboardDistribution(maxAwarded, players.size());
        players.forEach(player -> playerReports.put(player, new ModifiablePlayerReport(player)));
        game.sendInputToServer(server -> server.onQuestionReleased(pickedQuestion));
    }

    @Override
    public PlayerGuessContext onGuessReceived(Player<?> source, String message) {

        playerReports.get(source).registerSolution(message);
        Optional<Double> optCorrectness = pickedQuestion.evaluateAnswer(Answer.fromString(message));
        if(optCorrectness.isEmpty()) {
            return new PlayerGuessContext(source, 0, true);
        }

        double correctness = optCorrectness.get();

        counter.incrementGuess(source);
        leaderboard.scoreUpdate(source, correctness);
        playerReports.get(source).setReward(correctness);

        if(Math.abs(correctness - 1) < ScoreDistribution.EPSILON) {
            counter.consumeAllGuesses(source);
        }

        return new PlayerGuessContext(source, correctness, counter.isEligible(source));
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
        return new NoGuessLeft(counter, players).or(new FixedLeaderboardEnding(leaderboard, players.size()));
    }

    @Override
    public ScoreDistribution initScoreDistribution() {
        return leaderboard;
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
