package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.GameUpdatable;
import net.starype.quiz.api.game.round.*;
import net.starype.quiz.api.game.question.Question;

import java.util.concurrent.TimeUnit;

public class TimedRaceRoundFactory {
    public QuizRound create(Question question, int maxGuesses,
                            double scoreForWinner, long time, TimeUnit unit) {

        IsGuessValid isGuessValid = new IsGuessValid();

        GuessCounter counter = new GuessCounter(maxGuesses);
        MaxGuess maxGuess = new MaxGuess(counter);
        RoundState roundState = new RoundState(counter, maxGuess);

        SwitchPredicate timeOutEnding = new SwitchPredicate(false, roundState);
        GameUpdatable quizTimer = new QuizTimer(unit, time);
        quizTimer.addEventListener(timeOutEnding);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessValid)
                        .followedBy(new IncrementPlayerGuess())
                        .followedBy(new ConsumeAllPlayersGuess().withCondition(new IsCorrectnessOne()))
                        .followedBy(new UpdatePlayerEligibility());

        StandardRound round = new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .withScoreDistribution(new BinaryDistribution(roundState.getLeaderboard(), scoreForWinner))
                .withPlayerEligibility(maxGuess)
                .withRoundState(roundState)
                .addEvent(quizTimer)
                .withEndingCondition(new NoPlayerEligible(roundState).or(timeOutEnding))
                .build();

        quizTimer.addEventListener(round::checkEndOfRound);

        return round;
    }
}
