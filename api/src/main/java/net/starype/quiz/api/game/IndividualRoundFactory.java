package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.*;
import net.starype.quiz.api.game.question.Question;

public class IndividualRoundFactory {
    public QuizRound create(Question question, double maxToAward) {

        IsGuessValid isGuessValid = new IsGuessValid();

        GuessCounter counter = new GuessCounter(1);
        MaxGuess maxGuess = new MaxGuess(counter);
        RoundState roundState = new RoundState(counter, maxGuess);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessValid.negate())
                        .followedBy(new MakePlayerEligible().withCondition(isGuessValid.negate()))
                        .followedBy(new UpdateLeaderboard().withCondition(isGuessValid))
                        .followedBy(new IncrementPlayerGuess().withCondition(isGuessValid))
                        .followedBy(new UpdatePlayerEligibility().withCondition(isGuessValid));

        return new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new AddCorrectnessIfNew())
                .withRoundState(roundState)
                .withEndingCondition(new NoPlayerEligible(roundState))
                .withQuestion(question)
                .withScoreDistribution(new OneTryDistribution(maxToAward))
                .build();
    }
}
