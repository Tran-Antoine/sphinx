package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.question.Question;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class ClassicalRoundFactory {

    public StandardRound create(Question question, double maxAwarded, int maxGuesses) {

        IsGuessEmpty isGuessEmpty = new IsGuessEmpty();
        BiPredicate<RoundState, SettablePlayerGuessContext> isGuessEmptyPredicate = (t, u) -> isGuessEmpty.value();

        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter, counter);
        LeaderboardDistribution distribution = new LeaderboardDistribution(maxAwarded, roundState.getLeaderboard());
        NoGuessLeft noGuessLeft = new NoGuessLeft(counter);
        FixedLeaderboardEnding fixedLeaderboardEnding = new FixedLeaderboardEnding(roundState.getLeaderboard());


        BiConsumer<RoundState, SettablePlayerGuessContext> consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmptyPredicate)
                        .andThen(new MakePlayerEligible().linkTo(isGuessEmptyPredicate))
                        .andThen(new IncrementPlayerGuess().linkTo(isGuessEmptyPredicate.negate()))
                        .andThen(new UpdateLeaderboard().linkTo(isGuessEmptyPredicate.negate()
                                .and(new IsCorrectnessZero().negate())))
                        .andThen(new ConsumePlayerGuess().linkTo(isGuessEmptyPredicate.negate().and(new IsCorrectnessZero())))
                        .andThen(new UpdatePlayerEligibility().linkTo(isGuessEmptyPredicate.negate()));

        return new StandardRound.Builder()
                .withGuessReceivedHead(isGuessEmpty)
                .withGuessReceivedConsumer(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .addScoreDistribution(distribution)
                .addPlayerEligibility(counter)
                .withRoundState(roundState)
                .withEndingCondition(noGuessLeft.or(fixedLeaderboardEnding))
                .addPlayerSettable(noGuessLeft)
                .addPlayerSettable(fixedLeaderboardEnding)
                .addPlayerSettable(roundState)
                .build();
    }
}
