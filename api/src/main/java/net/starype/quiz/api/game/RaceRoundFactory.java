package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.*;
import net.starype.quiz.api.game.question.Question;

public class RaceRoundFactory {
    public StandardRound create(Question question, int maxGuesses, double scoreForWinner) {

        IsGuessEmpty isGuessEmpty = new IsGuessEmpty();

        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter, counter);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessEmpty)
                        .followedBy(new MakePlayerEligible().withCondition(isGuessEmpty))
                        .followedBy(new IncrementPlayerGuess().withCondition(isGuessEmpty.negate()))
                        .followedBy(new ConsumeAllPlayersGuess()
                                .withCondition(new IsCorrectnessOne().and(isGuessEmpty.negate())))
                        .followedBy(new UpdatePlayerEligibility().withCondition(isGuessEmpty.negate())
                        .followedBy(new UpdateLeaderboard().withCondition(isGuessEmpty.negate())));

        return new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .withScoreDistribution(new BinaryDistribution(roundState.getLeaderboard(), scoreForWinner))
                .withPlayerEligibility(counter)
                .withRoundState(roundState)
                .withEndingCondition(new NoGuessLeft())
                .build();
    }
}
