package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.question.Question;

import java.util.function.BiPredicate;

public class RaceRoundFactory {
    public StandardRound create(Question question, int maxGuesses, double scoreForWinner) {

        IsGuessEmpty isGuessEmpty = new IsGuessEmpty();
        BiPredicate<RoundState, MutableGuessContext> isGuessEmptyPredicate = (t, u) -> isGuessEmpty.value();

        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter, counter);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmptyPredicate)
                        .followedBy(new MakePlayerEligible().linkTo(isGuessEmptyPredicate))
                        .followedBy(new IncrementPlayerGuess().linkTo(isGuessEmptyPredicate.negate()))
                        .followedBy(new ConsumeAllPlayersGuess()
                                .linkTo(new IsCorrectnessOne().and(isGuessEmptyPredicate.negate())))
                        .followedBy(new UpdatePlayerEligibility().linkTo(isGuessEmptyPredicate.negate())
                        .followedBy(new UpdateLeaderboard().linkTo(isGuessEmptyPredicate.negate())));

        return new StandardRound.Builder()
                .withGuessReceivedHead(isGuessEmpty)
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .addScoreDistribution(new BinaryDistribution(roundState.getLeaderboard(), scoreForWinner))
                .addPlayerEligibility(counter)
                .withRoundState(roundState)
                .withEndingCondition(new NoGuessLeft())
                .build();
    }
}
