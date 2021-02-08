package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.question.Question;

import java.util.function.BiPredicate;

public class IndividualRoundFactory {
    public StandardRound create(Question question, double maxToAward) {

        IsGuessEmpty isGuessEmpty = new IsGuessEmpty();
        BiPredicate<RoundState, MutableGuessContext> isGuessEmptyPredicate = (t, u) -> isGuessEmpty.value();

        MaxGuessCounter counter = new MaxGuessCounter(1);
        RoundState roundState = new RoundState(counter, counter);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmptyPredicate)
                        .followedBy(new MakePlayerEligible().linkTo(isGuessEmptyPredicate))
                        .followedBy(new UpdateLeaderboard().linkTo(isGuessEmptyPredicate.negate()))
                        .followedBy(new IncrementPlayerGuess().linkTo(isGuessEmptyPredicate.negate()))
                        .followedBy(new UpdatePlayerEligibility().linkTo(isGuessEmptyPredicate.negate()));

        return new StandardRound.Builder()
                .withGuessReceivedHead(isGuessEmpty)
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new AddCorrectnessIfNew())
                .withRoundState(roundState)
                .withEndingCondition(new NoGuessLeft())
                .withQuestion(question)
                .addScoreDistribution(new OneTryDistribution(maxToAward))
                .addPlayerEligibility(counter)
                .build();
    }
}
