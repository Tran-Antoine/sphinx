package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessprocess.*;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class ClassicalRoundFactory {

    public StandardRound create(Question question, double maxAwarded, Collection<Player<?>> players) {
        BiPredicate<RoundState, SettablePlayerGuessContext> isGuessEmpty = (t, u) -> false;
        GuessReceivedHead headConsumer = new IsGuessEmpty().control(isGuessEmpty);

        BiConsumer<RoundState, SettablePlayerGuessContext> consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmpty)
                .andThen(new IncrementPlayerGuess().linkTo(isGuessEmpty.negate()))
                .andThen(new UpdateLeaderboard().linkTo(isGuessEmpty.negate())
                .andThen(new ConsumePlayerGuess().linkTo(isGuessEmpty.negate().and(new IsAnswerCorrect()))));


        return new StandardRound.Builder()
                .withGuessReceivedHead(headConsumer)
                .withGuessReceivedConsumer(consumer)
                .withQuestion(question)
                .addScoreDistribution(new LeaderboardDistribution(maxAwarded, players.size()))
                .build();
    }
}
