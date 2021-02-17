package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.*;
import net.starype.quiz.api.game.question.Question;

public class RaceRoundFactory {
    public StandardRound create(Question question, int maxGuesses, double scoreForWinner) {

        IsGuessEmpty isGuessEmpty = new IsGuessEmpty();

        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter, counter);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().linkTo(isGuessEmpty)
                        .followedBy(new MakePlayerEligible().linkTo(isGuessEmpty))
                        .followedBy(new IncrementPlayerGuess().linkTo(isGuessEmpty.negate()))
                        .followedBy(new ConsumeAllPlayersGuess()
                                .linkTo(new IsCorrectnessOne().and(isGuessEmpty.negate())))
                        .followedBy(new UpdatePlayerEligibility().linkTo(isGuessEmpty.negate())
                        .followedBy(new UpdateLeaderboard().linkTo(isGuessEmpty.negate())));

        return new StandardRound.Builder()
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
