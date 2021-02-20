package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.*;
import net.starype.quiz.api.game.question.Question;

public class PollRoundFactory {
    public QuizRound create(Question question, int maxGuesses) {
        GuessCounter counter = new GuessCounter(maxGuesses);
        MaxGuess maxGuess = new MaxGuess(counter);
        RoundState roundState = new RoundState(counter, maxGuess);

        GuessReceivedAction consumer =
                new UpdateAnswers()
                .followedBy(new IncrementPlayerGuess())
                .followedBy(new UpdatePlayerEligibility());


        return new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .withScoreDistribution(new ZeroScoreDistribution())
                .withPlayerEligibility(maxGuess)
                .withRoundState(roundState)
                .withEndingCondition(new NoPlayerEligible(roundState))
                .build();
    }
}
