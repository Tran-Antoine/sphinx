package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class ClassicalRoundFactory {

    public StandardRound create(Question question, double maxAwarded, Collection<? extends Player<?>> players,
                                int maxGuesses) {
        BiPredicate<RoundState, SettablePlayerGuessContext> isGuessEmpty = (t, u) -> false;
        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(players, counter, counter);
        LeaderboardDistribution distribution = new LeaderboardDistribution(maxAwarded, players.size(),
                roundState.getLeaderboard());

        BiConsumer<RoundState, SettablePlayerGuessContext> consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmpty)
                        .andThen(new MakePlayerEligible().linkTo(isGuessEmpty))
                        .andThen(new IncrementPlayerGuess().linkTo(isGuessEmpty.negate()))
                        .andThen(new UpdateLeaderboard().linkTo(isGuessEmpty.negate()
                                .and(new IsCorrectnessZero().negate())))
                        .andThen(new ConsumePlayerGuess().linkTo(isGuessEmpty.negate().and(new IsCorrectnessZero())))
                        .andThen(new UpdatePlayerEligibility().linkTo(isGuessEmpty.negate()));

        return new StandardRound.Builder()
                .withGuessReceivedHead(new IsGuessEmpty().control(isGuessEmpty))
                .withGuessReceivedConsumer(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .addScoreDistribution(distribution)
                .addPlayerEligibility(counter)
                .withRoundState(roundState)
                .withEndingCondition(new NoGuessLeft(counter, players)
                        .or(new FixedLeaderboardEnding(distribution, players.size())))
                .build();
    }
}
