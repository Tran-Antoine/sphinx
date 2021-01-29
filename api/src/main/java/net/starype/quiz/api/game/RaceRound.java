package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RaceRound implements GameRound {

    private final Question pickedQuestion;
    private final Map<IDHolder<?>, ModifiablePlayerReport> playerReports;
    private final double pointsToAward;
    private AtomicReference<IDHolder<?>> winnerContainer;
    private Collection<? extends IDHolder<?>> players;
    private GameRoundContext context;

    private final MaxGuessCounter counter;

    public RaceRound(int maxGuessesPerPlayer, Question pickedQuestion, double pointsToAward) {
        this.counter = new MaxGuessCounter(maxGuessesPerPlayer);
        this.pickedQuestion = pickedQuestion;
        this.pointsToAward = pointsToAward;
        playerReports = new HashMap<>();
    }

    @Override
    public void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler) {
        this.winnerContainer = new AtomicReference<>();
        this.players = players;
        this.context = new GameRoundContext(this);
        players.forEach(player -> playerReports.put(player, new ModifiablePlayerReport(player)));
        if(game != null) {
            game.sendInputToServer((server) -> server.onQuestionReleased(pickedQuestion));
        }
    }

    @Override
    public PlayerGuessContext onGuessReceived(Player<?> source, String message) {

        // eligibility checks are performed in the game class
        playerReports.get(source).registerSolution(message);
        Optional<Double> correctness = pickedQuestion.evaluateAnswer(Answer.fromString(message));
        if(correctness.isEmpty()) {
            return new PlayerGuessContext(source, 0, true);
        }

        boolean binaryCorrectness = Math.abs(correctness.get() - 1) < ScoreDistribution.EPSILON;
        playerReports.get(source).setReward(binaryCorrectness ? 1.0F : 0.0F);


        counter.incrementGuess(source);
        if(binaryCorrectness) {
            counter.consumeAllGuesses(source);
            winnerContainer.set(source);
        }

        return new PlayerGuessContext(source, binaryCorrectness ? 1.0 : 0.0, counter.isEligible(source));
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
        return new WinnerExists(winnerContainer).or(new NoGuessLeft(counter, players));
    }

    @Override
    public ScoreDistribution initScoreDistribution() {
        return winnerContainer.get() == null
                ? new ZeroScoreDistribution()
                : new SingleWinnerDistribution(winnerContainer, pointsToAward);
    }

    @Override
    public GameRoundReport initReport(List<Standing> standings) {
        return winnerContainer.get() == null
                ? winnerlessReport(standings)
                : winnerReport(standings);
    }

    private GameRoundReport winnerReport(List<Standing> standings) {
        return new SimpleGameReport(standings, pickedQuestion.getDisplayableCorrectAnswer(), playerReports.values());
    }

    private GameRoundReport winnerlessReport(List<Standing> standings) {
        return new SimpleGameReport(Collections.singletonList("No winner for this round"), standings,
                pickedQuestion.getDisplayableCorrectAnswer(), playerReports.values());
    }

    @Override
    public GameRoundContext getContext() {
        return context;
    }

    public static class Builder {

        private int maxGuessesPerPlayer;
        private Question pickedQuestion;
        private double pointsToAward;

        public Builder withMaxGuessesPerPlayer(int maxGuessesPerPlayer) {
            this.maxGuessesPerPlayer = maxGuessesPerPlayer;
            return this;
        }

        public Builder withQuestion(Question pickedQuestion) {
            this.pickedQuestion = pickedQuestion;
            return this;
        }

        public Builder withPointsToAward(double pointsToAward) {
            this.pointsToAward = pointsToAward;
            return this;
        }

        public RaceRound build() {
            return new RaceRound(maxGuessesPerPlayer, pickedQuestion, pointsToAward);
        }
    }
}
