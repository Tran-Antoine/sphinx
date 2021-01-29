package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.util.SortUtils;

import java.util.*;

public class ClassicalRound implements GameRound {

    private Question pickedQuestion;
    private Collection<? extends IDHolder<?>> players;
    private MaxGuessCounter counter;
    private double maxAwarded;
    private LeaderboardDistribution leaderboard;
    private List<ScoreDistribution> scoreDistributions = new ArrayList<>();

    public ClassicalRound(Question pickedQuestion, int maxGuesses, double maxAwarded) {
        this.pickedQuestion = pickedQuestion;
        this.counter = new MaxGuessCounter(maxGuesses);
        this.maxAwarded = maxAwarded;
    }

    @Override
    public void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler) {
        this.players = players;
        this.leaderboard = new LeaderboardDistribution(maxAwarded, players.size());
        scoreDistributions.add(leaderboard);
        game.sendInputToServer(server -> server.onQuestionReleased(pickedQuestion));
    }

    @Override
    public SettablePlayerGuessContext onGuessReceived(Player<?> source, String message) {

        Optional<Double> optCorrectness = pickedQuestion.evaluateAnswer(Answer.fromString(message));
        if(optCorrectness.isEmpty()) {
            return new SettablePlayerGuessContext(source, 0, true);
        }

        double correctness = optCorrectness.get();

        counter.incrementGuess(source);
        leaderboard.scoreUpdate(source, correctness);

        if(Math.abs(correctness - 1) < 0.001) {
            counter.consumeAllGuesses(source);
        }

        return new SettablePlayerGuessContext(source, correctness, counter.isEligible(source));
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
        return new NoGuessLeft(counter, players).or(new FixedLeaderboardEnding(leaderboard, players.size()));
    }

    @Override
    public List<ScoreDistribution> initScoreDistribution() {
        return scoreDistributions;
    }

    @Override
    public GameRoundReport initReport(Map<Player<?>, Double> standings) {
        return () -> createReport(standings);
    }

    private List<String> createReport(Map<Player<?>, Double> standings) {
        List<String> report = new ArrayList<>();
        report.add("Scores acquired for the round:\n");
        for(SortUtils.Standing standing : SortUtils.sortByScore(standings)) {
            report.add(standing.getPlayer().getNickname()+": " + standing.getScoreAcquired());
        }
        return report;
    }

    @Override
    public GameRoundContext getContext() {
        return new GameRoundContext(this);
    }
}
