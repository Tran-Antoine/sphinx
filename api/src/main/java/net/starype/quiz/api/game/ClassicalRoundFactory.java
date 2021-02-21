package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.*;
import net.starype.quiz.api.game.question.Question;

public class ClassicalRoundFactory {

    public QuizRound create(Question question, double maxAwarded, int maxGuesses) {

        IsGuessValid isGuessValid = new IsGuessValid();

        GuessCounter counter = new GuessCounter(maxGuesses);
        MaxGuess maxGuess = new MaxGuess(counter);
        RoundState roundState = new RoundState(counter, maxGuess);
        LeaderboardDistribution distribution = new LeaderboardDistribution(maxAwarded, roundState.getLeaderboard());

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessValid.negate())
                        .followedBy(new MakePlayerEligible().withCondition(isGuessValid.negate()))
                        .followedBy(new IncrementPlayerGuess().withCondition(isGuessValid))
                        .followedBy(new UpdateLeaderboard().withCondition(isGuessValid))
                        .followedBy(new ConsumePlayerGuess().withCondition(isGuessValid.and(new IsCorrectnessZero())))
                        .followedBy(new UpdatePlayerEligibility().withCondition(isGuessValid));

        return new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .withScoreDistribution(distribution)
                .withRoundState(roundState)
                .withEndingCondition(new NoPlayerEligible(roundState).or(new FixedLeaderboardEnding(roundState)))
                .build();
    }
}
