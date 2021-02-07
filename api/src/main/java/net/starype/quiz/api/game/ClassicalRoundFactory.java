package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.question.Question;

import java.util.function.BiPredicate;

public class ClassicalRoundFactory {

    public StandardRound create(Question question, double maxAwarded, int maxGuesses) {

        IsGuessEmpty isGuessEmpty = new IsGuessEmpty();
        BiPredicate<RoundState, SettablePlayerGuessContext> isGuessEmptyPredicate = (t, u) -> isGuessEmpty.get();

        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter, counter);
        LeaderboardDistribution distribution = new LeaderboardDistribution(maxAwarded, roundState.getLeaderboard());
        NoGuessLeft noGuessLeft = new NoGuessLeft(counter);
        FixedLeaderboardEnding fixedLeaderboardEnding = new FixedLeaderboardEnding(roundState.getLeaderboard());


        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmptyPredicate)
                        .followedBy(new MakePlayerEligible().linkTo(isGuessEmptyPredicate))
                        .followedBy(new IncrementPlayerGuess().linkTo(isGuessEmptyPredicate.negate()))
                        .followedBy(new UpdateLeaderboard().linkTo(isGuessEmptyPredicate.negate()
                                .and(new IsCorrectnessZero().negate())))
                        .followedBy(new ConsumePlayerGuess().linkTo(isGuessEmptyPredicate.negate().and(new IsCorrectnessZero())))
                        .followedBy(new UpdatePlayerEligibility().linkTo(isGuessEmptyPredicate.negate()));

        return new StandardRound.Builder()
                .withGuessReceivedHead(isGuessEmpty)
                .withGuessReceivedAction(consumer)
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
