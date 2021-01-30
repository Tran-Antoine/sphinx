package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.AddAnswer;
import net.starype.quiz.api.game.guessreceived.ConsumePlayerGuess;
import net.starype.quiz.api.game.guessreceived.IncrementPlayerGuess;
import net.starype.quiz.api.game.guessreceived.RoundState;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.Collection;

public class PollRoundFactory {
    public StandardRound create(Question question, Collection<Player<?>> players, int maxGuesses) {
        MaxGuessCounter counter = new MaxGuessCounter(maxGuesses);
        RoundState roundState = new RoundState(players, counter);

        return new StandardRound.Builder()
                .withGuessReceivedHead(new AddAnswer())
                .withGuessReceivedConsumer(new IncrementPlayerGuess())
                .withGiveUpReceivedConsumer(new ConsumePlayerGuess())
                .withQuestion(question)
                .addScoreDistribution(new ZeroScoreDistribution())
                .addPlayerEligibility(counter)
                .withRoundState(roundState)
                .withEndingCondition(new NoGuessLeft(counter, players))
                .build();
    }
}
