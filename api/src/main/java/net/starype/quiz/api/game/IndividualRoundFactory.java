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
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessValid)
                        .followedBy(new MakePlayerEligible().withCondition(isGuessValid))
                        .followedBy(new UpdateLeaderboard().withCondition(isGuessValid.negate()))
                        .followedBy(new IncrementPlayerGuess().withCondition(isGuessValid.negate()))
                        .followedBy(new UpdatePlayerEligibility().withCondition(isGuessValid.negate()));

        return new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new AddCorrectnessIfNew())
                .withRoundState(roundState)
                .withEndingCondition(new NoPlayerEligible(roundState))
                .withQuestion(question)
                .withScoreDistribution(new OneTryDistribution(maxToAward))
                .withPlayerEligibility(maxGuess)
                .build();
    }
}
