package net.starype.quiz.api.round;

import net.starype.quiz.api.game.GuessCounter;
import net.starype.quiz.api.game.MaxGuess;
import net.starype.quiz.api.game.OneTryDistribution;
import net.starype.quiz.api.question.Question;

public class IndividualRoundFactory {
    public QuizRound create(Question question, double maxToAward) {

        IsGuessValid isGuessValid = new IsGuessValid();

        GuessCounter counter = new GuessCounter(1);
        RoundState roundState = new RoundState(counter);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessValid.negate())
                        .followedBy(new MakePlayerEligible().withCondition(isGuessValid.negate()))
                        .followedBy(new UpdateLeaderboard().withCondition(isGuessValid))
                        .followedBy(new IncrementPlayerGuess().withCondition(isGuessValid));

        return new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new AddCorrectnessIfNew().followedBy(new ConsumePlayerGuess()))
                .withRoundState(roundState)
                .withQuestion(question)
                .withPlayerEligibility(new MaxGuess(counter))
                .withScoreDistribution(new OneTryDistribution(maxToAward, roundState.getLeaderboard()))
                .build();
    }
}
