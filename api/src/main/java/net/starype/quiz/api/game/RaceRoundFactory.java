package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class RaceRoundFactory {
    public StandardRound create(Question question, Collection<? extends Player<?>> players,
                                int maxGuesses, double scoreForWinner) {

        BiPredicate<RoundState, SettablePlayerGuessContext> isGuessEmpty = (t, u) -> false;
        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(players, counter, counter);

        BiConsumer<RoundState, SettablePlayerGuessContext> consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmpty)
                        .andThen(new IncrementPlayerGuess())
                        .andThen(new ConsumeAllPlayersGuess().linkTo(new IsCorrectnessOne()))
                        .andThen(new UpdatePlayerEligibility());

        return new StandardRound.Builder()
                .withGuessReceivedHead(new IsGuessEmpty().control(isGuessEmpty))
                .withGuessReceivedConsumer(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .addScoreDistribution(new BinaryDistribution(roundState.getLeaderboard(), scoreForWinner))
                .addPlayerEligibility(counter)
                .withRoundState(roundState)
                .withGuessEndingCondition(new NoGuessLeft(counter, players))
                .build();
    }
}
