package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.question.Question;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class RaceRoundFactory {
    public StandardRound create(Question question, int maxGuesses, double scoreForWinner) {

        IsGuessEmpty isGuessEmpty = new IsGuessEmpty();
        BiPredicate<RoundState, SettablePlayerGuessContext> isGuessEmptyPredicate = (t, u) -> isGuessEmpty.value();

        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter, counter);
        NoGuessLeft noGuessLeft = new NoGuessLeft(counter);

        BiConsumer<RoundState, SettablePlayerGuessContext> consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmptyPredicate)
                        .andThen(new MakePlayerEligible().linkTo(isGuessEmptyPredicate))
                        .andThen(new IncrementPlayerGuess().linkTo(isGuessEmptyPredicate.negate()))
                        .andThen(new ConsumeAllPlayersGuess()
                                .linkTo(new IsCorrectnessOne().and(isGuessEmptyPredicate.negate())))
                        .andThen(new UpdatePlayerEligibility().linkTo(isGuessEmptyPredicate.negate())
                        .andThen(new UpdateLeaderboard().linkTo(isGuessEmptyPredicate.negate())));

        return new StandardRound.Builder()
                .withGuessReceivedHead(isGuessEmpty)
                .withGuessReceivedConsumer(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .addScoreDistribution(new BinaryDistribution(roundState.getLeaderboard(), scoreForWinner))
                .addPlayerEligibility(counter)
                .withRoundState(roundState)
                .withEndingCondition(noGuessLeft)
                .addPlayerSettable(roundState)
                .addPlayerSettable(noGuessLeft)
                .build();
    }
}
