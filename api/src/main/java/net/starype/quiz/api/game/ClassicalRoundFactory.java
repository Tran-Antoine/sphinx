package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.*;
import net.starype.quiz.api.game.question.Question;

public class ClassicalRoundFactory {

    public StandardRound create(Question question, double maxAwarded, int maxGuesses) {

        IsGuessEmpty isGuessEmpty = new IsGuessEmpty();

        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter, counter);
        LeaderboardDistribution distribution = new LeaderboardDistribution(maxAwarded, roundState.getLeaderboard());

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessEmpty)
                        .followedBy(new MakePlayerEligible().withCondition(isGuessEmpty))
                        .followedBy(new IncrementPlayerGuess().withCondition(isGuessEmpty.negate()))
                        .followedBy(new UpdateLeaderboard().withCondition(isGuessEmpty.negate()
                                .and(new IsCorrectnessZero().negate())))
                        .followedBy(new ConsumePlayerGuess().withCondition(isGuessEmpty.negate().and(new IsCorrectnessZero())))
                        .followedBy(new UpdatePlayerEligibility().withCondition(isGuessEmpty.negate()));

        return new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .withScoreDistribution(distribution)
                .withPlayerEligibility(counter)
                .withRoundState(roundState)
                .withEndingCondition(new NoGuessLeft().or(new FixedLeaderboardEnding()))
                .build();
    }
}
