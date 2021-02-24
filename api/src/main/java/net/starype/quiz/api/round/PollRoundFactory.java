package net.starype.quiz.api.round;

import net.starype.quiz.api.game.GuessCounter;
import net.starype.quiz.api.game.MaxGuess;
import net.starype.quiz.api.game.ZeroScoreDistribution;
import net.starype.quiz.api.question.Question;

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
