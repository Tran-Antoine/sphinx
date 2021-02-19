package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.*;
import net.starype.quiz.api.game.question.Question;

public class IndividualRoundFactory {
    public GameRound create(Question question, double maxToAward) {

        IsGuessValid isGuessValid = new IsGuessValid();

        MaxGuessCounter counter = new MaxGuessCounter(1);
        RoundState roundState = new RoundState(counter, counter);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessValid)
                        .followedBy(new MakePlayerEligible().withCondition(isGuessValid))
                        .followedBy(new UpdateLeaderboard().withCondition(isGuessValid.negate()))
                        .followedBy(new IncrementPlayerGuess().withCondition(isGuessValid.negate()))
                        .followedBy(new UpdatePlayerEligibility().withCondition(isGuessValid.negate()));

        return new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new AddCorrectnessIfNew())
                .withRoundState(roundState)
                .withEndingCondition(new NoGuessLeft(roundState))
                .withQuestion(question)
                .withScoreDistribution(new OneTryDistribution(maxToAward))
                .withPlayerEligibility(counter)
                .build();
    }
}
