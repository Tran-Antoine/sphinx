package net.starype.quiz.api.game;

import net.starype.quiz.api.round.*;
import net.starype.quiz.api.question.Question;

public class ClassicalRoundFactory {

    public QuizRound create(Question question, double maxAwarded, int maxGuesses) {

        IsGuessValid isGuessValid = new IsGuessValid();

        GuessCounter counter = new GuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter);
        LeaderboardDistribution distribution = new LeaderboardDistribution(maxAwarded, roundState.getLeaderboard());

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessValid.negate())
                        .followedBy(new MakePlayerEligible().withCondition(isGuessValid.negate()))
                        .followedBy(new IncrementPlayerGuess().withCondition(isGuessValid))
                        .followedBy(new UpdateLeaderboard().withCondition(isGuessValid))
                        .followedBy(new ConsumePlayerGuess().withCondition(isGuessValid.and(new IsCorrectnessZero())));

        return new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .withScoreDistribution(distribution)
                .withRoundState(roundState)
                .withPlayerEligibility(new MaxGuess(counter))
                .withEndingCondition(new FixedLeaderboardEnding(roundState))
                .build();
    }
}
