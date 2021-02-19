package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.*;
import net.starype.quiz.api.game.question.Question;

public class RaceRoundFactory {
    public GameRound create(Question question, int maxGuesses, double scoreForWinner) {

        IsGuessValid isGuessValid = new IsGuessValid();

        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter, counter);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessValid)
                        .followedBy(new MakePlayerEligible().withCondition(isGuessValid))
                        .followedBy(new IncrementPlayerGuess().withCondition(isGuessValid.negate()))
                        .followedBy(new ConsumeAllPlayersGuess()
                                .withCondition(new IsCorrectnessOne().and(isGuessValid.negate())))
                        .followedBy(new UpdatePlayerEligibility().withCondition(isGuessValid.negate())
                        .followedBy(new UpdateLeaderboard().withCondition(isGuessValid.negate())));

        return new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .withScoreDistribution(new BinaryDistribution(roundState.getLeaderboard(), scoreForWinner))
                .withPlayerEligibility(counter)
                .withRoundState(roundState)
                .withEndingCondition(new NoGuessLeft(roundState))
                .build();
    }
}
