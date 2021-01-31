package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class IndividualRoundFactory {
    public StandardRound create(Question question, Collection<? extends Player<?>> players,
                                int maxGuesses, int maxToAward) {

        BiPredicate<RoundState, SettablePlayerGuessContext> isGuessEmpty = (t, u) -> false;
        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(players, counter, counter);

        BiConsumer<RoundState, SettablePlayerGuessContext> consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmpty)
                        .andThen(new MakePlayerEligible().linkTo(isGuessEmpty))
                        .andThen(new UpdateLeaderboard().linkTo(isGuessEmpty.negate()))
                        .andThen(new IncrementPlayerGuess().linkTo(isGuessEmpty.negate()))
                        .andThen(new UpdatePlayerEligibility().linkTo(isGuessEmpty.negate()));

        return new StandardRound.Builder()
                .withGuessReceivedHead(new IsGuessEmpty().control(isGuessEmpty))
                .withGuessReceivedConsumer(consumer)
                .withGiveUpReceivedConsumer(new AddCorrectnessIfNew())
                .withRoundState(roundState)
                .withEndingCondition(new NoGuessLeft(counter, players))
                .withQuestion(question)
                .addScoreDistribution(new OneTryDistribution(maxToAward))
                .addPlayerEligibility(counter)
                .build();
    }
}
