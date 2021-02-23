package net.starype.quiz.api.game;

import net.starype.quiz.api.round.*;
import net.starype.quiz.api.question.Question;

public class RaceRoundFactory {
    public QuizRound create(Question question, int maxGuesses, double scoreForWinner) {

        IsGuessValid isGuessValid = new IsGuessValid();

        GuessCounter counter = new GuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter);

            GuessReceivedAction consumer =
                    new InvalidateCurrentPlayerCorrectness().withCondition(isGuessValid.negate())
                            .followedBy(new MakePlayerEligible().withCondition(isGuessValid.negate()))
                            .followedBy(new IncrementPlayerGuess().withCondition(isGuessValid))
                            .followedBy(new ConsumeAllPlayersGuess()
                                    .withCondition(new IsCorrectnessOne().and(isGuessValid)))
                            .followedBy(new UpdateLeaderboard().withCondition(isGuessValid));

            return new StandardRound.Builder()
                    .withGuessReceivedAction(consumer)
                    .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                    .withQuestion(question)
                    .withScoreDistribution(new BinaryDistribution(roundState.getLeaderboard(), scoreForWinner))
                    .withRoundState(roundState)
                    .withPlayerEligibility(new MaxGuess(counter))
                    .build();
    }
}
