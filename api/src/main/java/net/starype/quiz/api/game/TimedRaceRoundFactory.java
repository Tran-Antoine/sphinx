package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.question.Question;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class TimedRaceRoundFactory {
    public StandardRound create(Question question, int maxGuesses,
                                double scoreForWinner, long time, TimeUnit unit) {

        IsGuessEmpty isGuessEmpty = new IsGuessEmpty();
        BiPredicate<RoundState, SettablePlayerGuessContext> isGuessEmptyPredicate = (t, u) -> isGuessEmpty.value();

        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter, counter);
        NoGuessLeft noGuessLeft = new NoGuessLeft(counter);

        FalseToTruePredicate timeOutEnding = new FalseToTruePredicate();
        Timer timer = new Timer(unit, time);
        timer.addEventListener(timeOutEnding);

        BiConsumer<RoundState, SettablePlayerGuessContext> consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmptyPredicate)
                        .andThen(new IncrementPlayerGuess())
                        .andThen(new ConsumeAllPlayersGuess().linkTo(new IsCorrectnessOne()))
                        .andThen(new UpdatePlayerEligibility());

        StandardRound round = new StandardRound.Builder()
                .withGuessReceivedHead(isGuessEmpty)
                .withGuessReceivedConsumer(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .addScoreDistribution(new BinaryDistribution(roundState.getLeaderboard(), scoreForWinner))
                .addPlayerEligibility(counter)
                .withRoundState(roundState)
                .addEvent(timer)
                .withEndingCondition(noGuessLeft.or(timeOutEnding))
                .addPlayerSettable(noGuessLeft)
                .addPlayerSettable(roundState)
                .build();

        timer.addEventListener(new CheckEndOfRound(round::checkEndOfRound));

        return round;
    }
}
