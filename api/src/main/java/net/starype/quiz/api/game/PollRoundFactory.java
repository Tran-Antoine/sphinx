package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.*;
import net.starype.quiz.api.game.question.Question;

public class PollRoundFactory {
    public QuizRound create(Question question, int maxGuesses) {
        GuessCounter counter = new GuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter);

        GuessReceivedAction consumer =
                new UpdateAnswers()
                .followedBy(new IncrementPlayerGuess());


        return new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .withScoreDistribution(new ZeroScoreDistribution())
                .withRoundState(roundState)
                .withPlayerEligibility(new MaxGuess(counter))
                .build();
    }
}
