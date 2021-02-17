package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.*;
import net.starype.quiz.api.game.question.Question;

public class PollRoundFactory {
    public StandardRound create(Question question, int maxGuesses) {
        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter, counter);

        GuessReceivedAction consumer =
                new UpdateAnswers()
                .followedBy(new IncrementPlayerGuess())
                .followedBy(new UpdatePlayerEligibility());


        return new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .withScoreDistribution(new ZeroScoreDistribution())
                .withPlayerEligibility(counter)
                .withRoundState(roundState)
                .withEndingCondition(new NoGuessLeft())
                .build();
    }
}
