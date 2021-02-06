package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class TimedRaceRoundFactory {
    public StandardRound create(Question question, Collection<? extends Player<?>> players, int maxGuesses,
                                double scoreForWinner, long time, TimeUnit unit) {

        BiPredicate<RoundState, SettablePlayerGuessContext> isGuessEmpty = (t, u) -> false;
        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(players, counter, counter);

        FalseToTruePredicate timeOutEnding = new FalseToTruePredicate();
        Timer timer = new Timer(unit, time);
        timer.addEventListener(timeOutEnding);

        BiConsumer<RoundState, SettablePlayerGuessContext> consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmpty)
                        .andThen(new IncrementPlayerGuess())
                        .andThen(new ConsumeAllPlayersGuess().linkTo(new IsCorrectnessOne()))
                        .andThen(new UpdatePlayerEligibility());

        StandardRound round = new StandardRound.Builder()
                .withGuessReceivedHead(new IsGuessEmpty().control(isGuessEmpty))
                .withGuessReceivedConsumer(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .addScoreDistribution(new BinaryDistribution(roundState.getLeaderboard(), scoreForWinner))
                .addPlayerEligibility(counter)
                .withRoundState(roundState)
                .addEvent(timer)
                .withEndingCondition(new NoGuessLeft(counter, players).or(timeOutEnding))
                .build();

        timer.addEventListener(new CheckEndOfRound(round::checkEndOfRound));

        return round;
    }
}
