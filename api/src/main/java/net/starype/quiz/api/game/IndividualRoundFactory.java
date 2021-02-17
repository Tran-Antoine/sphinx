package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.*;
import net.starype.quiz.api.game.question.Question;

public class IndividualRoundFactory {
    public StandardRound create(Question question, double maxToAward) {

        IsGuessEmpty isGuessEmpty = new IsGuessEmpty();

        MaxGuessCounter counter = new MaxGuessCounter(1);
        RoundState roundState = new RoundState(counter, counter);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessEmpty)
                        .followedBy(new MakePlayerEligible().withCondition(isGuessEmpty))
                        .followedBy(new UpdateLeaderboard().withCondition(isGuessEmpty.negate()))
                        .followedBy(new IncrementPlayerGuess().withCondition(isGuessEmpty.negate()))
                        .followedBy(new UpdatePlayerEligibility().withCondition(isGuessEmpty.negate()));

        return new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new AddCorrectnessIfNew())
                .withRoundState(roundState)
                .withEndingCondition(new NoGuessLeft())
                .withQuestion(question)
                .withScoreDistribution(new OneTryDistribution(maxToAward))
                .withPlayerEligibility(counter)
                .build();
    }
}
