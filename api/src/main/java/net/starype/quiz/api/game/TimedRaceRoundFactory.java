package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.*;
import net.starype.quiz.api.game.question.Question;

import java.util.concurrent.TimeUnit;

public class TimedRaceRoundFactory {
    public StandardRound create(Question question, int maxGuesses,
                                double scoreForWinner, long time, TimeUnit unit) {

        IsGuessValid isGuessValid = new IsGuessValid();

        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter, counter);

        SwitchPredicate timeOutEnding = new SwitchPredicate(false);
        Timer timer = new Timer(unit, time);
        timer.addEventListener(timeOutEnding);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessValid)
                        .followedBy(new IncrementPlayerGuess())
                        .followedBy(new ConsumeAllPlayersGuess().withCondition(new IsCorrectnessOne()))
                        .followedBy(new UpdatePlayerEligibility());

        StandardRound round = new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .withScoreDistribution(new BinaryDistribution(roundState.getLeaderboard(), scoreForWinner))
                .withPlayerEligibility(counter)
                .withRoundState(roundState)
                .addEvent(timer)
                .withEndingCondition(new NoGuessLeft().or(timeOutEnding))
                .build();

        timer.addEventListener(new Callback(round::checkEndOfRound));

        return round;
    }
}
