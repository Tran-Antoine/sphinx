package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class ClassicalRoundFactory {

    public StandardRound create(Question question, double maxAwarded, Collection<Player<?>> players,
                                int maxGuesses) {
        BiPredicate<RoundState, SettablePlayerGuessContext> isGuessEmpty = (t, u) -> false;
        GuessReceivedHead headConsumer = new IsGuessEmpty().control(isGuessEmpty);
        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(players, counter);

        BiConsumer<RoundState, SettablePlayerGuessContext> consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmpty)
                .andThen(new IncrementPlayerGuess().linkTo(isGuessEmpty.negate()))
                .andThen(new UpdateLeaderboard().linkTo(isGuessEmpty.negate())
                .andThen(new ConsumePlayerGuess().linkTo(isGuessEmpty.negate().and(new IsAnswerCorrect()))));


        return new StandardRound.Builder()
                .withGuessReceivedHead(headConsumer)
                .withGuessReceivedConsumer(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .addScoreDistribution(new LeaderboardDistribution(maxAwarded, players.size()))
                .addPlayerEligibility(counter)
                .withRoundState(roundState)
                .withEndingCondition(new NoGuessLeft(counter, players))
                .build();
    }
}
