package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.question.Question;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class IndividualRoundFactory {
    public StandardRound create(Question question, double maxToAward) {

        IsGuessEmpty isGuessEmpty = new IsGuessEmpty();
        BiPredicate<RoundState, SettablePlayerGuessContext> isGuessEmptyPredicate = (t, u) -> isGuessEmpty.value();

        MaxGuessCounter counter = new MaxGuessCounter(1);
        RoundState roundState = new RoundState(counter, counter);
        NoGuessLeft noGuessLeft = new NoGuessLeft(counter);

        BiConsumer<RoundState, SettablePlayerGuessContext> consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmptyPredicate)
                        .andThen(new MakePlayerEligible().linkTo(isGuessEmptyPredicate))
                        .andThen(new UpdateLeaderboard().linkTo(isGuessEmptyPredicate.negate()))
                        .andThen(new IncrementPlayerGuess().linkTo(isGuessEmptyPredicate.negate()))
                        .andThen(new UpdatePlayerEligibility().linkTo(isGuessEmptyPredicate.negate()));

        return new StandardRound.Builder()
                .withGuessReceivedHead(isGuessEmpty)
                .withGuessReceivedConsumer(consumer)
                .withGiveUpReceivedConsumer(new AddCorrectnessIfNew())
                .withRoundState(roundState)
                .withEndingCondition(noGuessLeft)
                .withQuestion(question)
                .addScoreDistribution(new OneTryDistribution(maxToAward))
                .addPlayerEligibility(counter)
                .addPlayerSettable(roundState)
                .addPlayerSettable(noGuessLeft)
                .build();
    }
}
