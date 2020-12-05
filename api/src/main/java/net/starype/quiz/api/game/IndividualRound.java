package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class IndividualRound implements GameRound {

    private double maxToAward;
    private OneTryDistribution scoreDistribution;
    private Question pickedQuestion;
    private MaxGuessCounter maxGuessCounter;
    private Collection<? extends IDHolder<?>> players;

    public IndividualRound(Question pickedQuestion, double maxToAward) {
        this.maxToAward = maxToAward;
        this.pickedQuestion = pickedQuestion;
    }

    @Override
    public void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler) {
        this.scoreDistribution = new OneTryDistribution(maxToAward);
        this.players = players;
        this.maxGuessCounter = new MaxGuessCounter(1);
        game.sendInputToServer((server) -> server.onQuestionReleased(pickedQuestion));
    }

    @Override
    public PlayerGuessContext onGuessReceived(Player<?> source, String message) {
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
    public void onGiveUpReceived(IDHolder<?> source) {
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
        return scoreDistribution;
    }

    @Override
    public GameRoundReport initReport(Map<Player<?>, Double> standings) {
        return () -> createReport(standings);
    }

    private List<String> createReport(Map<Player<?>, Double> standings) {
        List<String> report = new ArrayList<>();
        List<Double> values = standings
                .values()
                .stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        for(double value : values) {
            Set<String> playersMatchingValue = standings
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().equals(value))
                    .map(Entry::getKey)
                    .map(Player::getNickname)
                    .collect(Collectors.toSet());
            report.add(String.join(", ", playersMatchingValue) + " got " + value + " points");
        }
        return report;
    }

    @Override
    public GameRoundContext getContext() {
        return new GameRoundContext(this);
    }
}
