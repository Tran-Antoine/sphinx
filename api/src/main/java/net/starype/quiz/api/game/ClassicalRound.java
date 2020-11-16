package net.starype.quiz.api.game;

import net.starype.quiz.api.FixedLeaderboardEnding;
import net.starype.quiz.api.game.LeaderboardDistribution.LeaderboardPosition;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.UUIDHolder;
import net.starype.quiz.api.game.question.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ClassicalRound implements GameRound {

    private Question pickedQuestion;
    private Collection<? extends UUIDHolder> players;
    private MaxGuessCounter counter;
    private double maxAwarded;
    private LeaderboardDistribution leaderboard;
    private QuizGame game;

    public ClassicalRound(Question pickedQuestion, int maxGuesses, double maxAwarded) {
        this.pickedQuestion = pickedQuestion;
        this.counter = new MaxGuessCounter(maxGuesses);
        this.maxAwarded = maxAwarded;
    }

    @Override
    public void start(QuizGame game, Collection<? extends UUIDHolder> players, EventHandler eventHandler) {
        this.game = game;
        this.players = players;
        this.leaderboard = new LeaderboardDistribution(maxAwarded, players.size());
    }

    @Override
    public void onGuessReceived(UUIDHolder source, String message) {

        Optional<Double> optCorrectness = pickedQuestion.evaluateAnswer(Answer.fromString(message));
        if(optCorrectness.isEmpty()) {
            return;
        }

        double correctness = optCorrectness.get();

        counter.incrementGuess(source);
        leaderboard.scoreUpdate(source, correctness);

        if(Math.abs(correctness - 1) < 0.001) {
            counter.consumeAllGuesses(source);
        }

        PlayerGuessContext context = new PlayerGuessContext(source, correctness, counter.isEligible(source));
        game.sendInputToServer((server) -> server.onPlayerGuessed(context));
    }

    @Override
    public void onGiveUpReceived(UUIDHolder source) {
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
            report.add(position.getPlayer().getUUID()+": " + position.getScore());
        }
        return report;
    }

    @Override
    public GameRoundContext getContext() {
        return new GameRoundContext(this);
    }
}
