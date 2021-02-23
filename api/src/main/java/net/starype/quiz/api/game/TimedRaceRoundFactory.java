package net.starype.quiz.api.game;

import net.starype.quiz.api.event.GameUpdatable;
import net.starype.quiz.api.round.*;
import net.starype.quiz.api.question.Question;

import java.util.concurrent.TimeUnit;

public class TimedRaceRoundFactory {
    public QuizRound create(Question question, int maxGuesses,
                            double scoreForWinner, long time, TimeUnit unit) {

        IsGuessValid isGuessValid = new IsGuessValid();

        GuessCounter counter = new GuessCounter(maxGuesses);
        RoundState roundState = new RoundState(counter);

        SwitchPredicate timeOutEnding = new SwitchPredicate(false, roundState);
        GameUpdatable quizTimer = new QuizTimer(unit, time);
        quizTimer.addEventListener(timeOutEnding);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessValid.negate())
                        .followedBy(new IncrementPlayerGuess())
                        .followedBy(new ConsumeAllPlayersGuess().withCondition(new IsCorrectnessOne()));

        StandardRound round = new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .withScoreDistribution(new BinaryDistribution(roundState.getLeaderboard(), scoreForWinner))
                .withRoundState(roundState)
                .addEvent(quizTimer)
                .withEndingCondition(timeOutEnding)
                .withPlayerEligibility(new MaxGuess(counter))
                .build();

        quizTimer.addEventListener(round::checkEndOfRound);

        return round;
    }
}
