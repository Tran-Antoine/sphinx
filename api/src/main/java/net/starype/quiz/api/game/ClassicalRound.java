package net.starype.quiz.api.game;

import net.starype.quiz.api.game.LeaderboardDistribution.LeaderboardPosition;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.question.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ClassicalRound implements GameRound {

    private Question pickedQuestion;
    private Collection<? extends IDHolder<?>> players;
    private MaxGuessCounter counter;
    private double maxAwarded;
    private LeaderboardDistribution leaderboard;

    public ClassicalRound(Question pickedQuestion, int maxGuesses, double maxAwarded) {
        this.pickedQuestion = pickedQuestion;
        this.counter = new MaxGuessCounter(maxGuesses);
        this.maxAwarded = maxAwarded;
    }

    @Override
    public void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler) {
        this.players = players;
        this.leaderboard = new LeaderboardDistribution(maxAwarded, players.size());
    }

    @Override
    public PlayerGuessContext onGuessReceived(IDHolder<?> source, String message) {

        Optional<Double> optCorrectness = pickedQuestion.evaluateAnswer(Answer.fromString(message));
        if(optCorrectness.isEmpty()) {
            return new PlayerGuessContext(source, 0, true);
        }

        double correctness = optCorrectness.get();

        counter.incrementGuess(source);
        leaderboard.scoreUpdate(source, correctness);

        if(Math.abs(correctness - 1) < 0.001) {
            counter.consumeAllGuesses(source);
        }

        return new PlayerGuessContext(source, correctness, counter.isEligible(source));
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
    public ScoreDistribution initScoreDistribution() {
        return leaderboard;
    }

    @Override
    public GameRoundReport initReport() {
        return this::createReport;
    }

    private List<String> createReport() {
        List<String> report = new ArrayList<>();
        for(LeaderboardPosition position : leaderboard.getLeaderboard()) {
            report.add(position.getPlayer().getId()+": " + position.getScore());
        }
        return report;
    }

    @Override
    public GameRoundContext getContext() {
        return new GameRoundContext(this);
    }
}
