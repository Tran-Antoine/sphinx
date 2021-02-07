package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.question.Question;

import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;

public class TimedRaceRoundFactory {
    public StandardRound create(Question question, int maxGuesses,
                                double scoreForWinner, long time, TimeUnit unit) {

        IsGuessEmpty isGuessEmpty = new IsGuessEmpty();
        BiPredicate<RoundState, SettablePlayerGuessContext> isGuessEmptyPredicate = (t, u) -> isGuessEmpty.get();

        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter, counter);
        NoGuessLeft noGuessLeft = new NoGuessLeft(counter);

        SwitchPredicate timeOutEnding = new SwitchPredicate(false);
        Timer timer = new Timer(unit, time);
        timer.addEventListener(timeOutEnding);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmptyPredicate)
                        .followedBy(new IncrementPlayerGuess())
                        .followedBy(new ConsumeAllPlayersGuess().linkTo(new IsCorrectnessOne()))
                        .followedBy(new UpdatePlayerEligibility());

        StandardRound round = new StandardRound.Builder()
                .withGuessReceivedHead(isGuessEmpty)
                .withGuessReceivedAction(consumer)
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
