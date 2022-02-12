package net.starype.quiz.discordimpl.game;

import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.api.event.GameUpdatable;
import net.starype.quiz.api.game.*;
import net.starype.quiz.api.player.IDHolder;
import net.starype.quiz.api.question.Question;
import net.starype.quiz.api.round.*;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class FlexiblePlayersRoundFactory {

    public QuizRound create(Question question, double maxToAward, TextChannel channel) {

        IsGuessValid isGuessValid = new IsGuessValid();

        GuessCounter counter = new GuessCounter(1);
        RoundState roundState = new RoundState(counter);

        SwitchPredicate timeOutEnding = new SwitchPredicate(false, roundState);
        GameUpdatable quizTimer = new DiscordQuizTimer(TimeUnit.SECONDS, 120, channel);
        quizTimer.addEventListener(timeOutEnding);

        GuessReceivedAction consumer =
                new InvalidateCurrentPlayerCorrectness().withCondition(isGuessValid.negate())
                        .followedBy(new MakePlayerEligible().withCondition(isGuessValid.negate()))
                        .followedBy(new UpdateLeaderboard().withCondition(isGuessValid))
                        .followedBy(new IncrementPlayerGuess().withCondition(isGuessValid));

        StandardRound round = new StandardRound.Builder()
                .withGuessReceivedAction(consumer)
                .withGiveUpReceivedConsumer(new AddCorrectnessIfNew().followedBy(new ConsumePlayerGuess()))
                .withRoundState(roundState)
                .withQuestion(question)
                .withEndingCondition(timeOutEnding)
                .withPlayerEligibility(new ModifiedMaxGuess(counter))
                .withScoreDistribution(new OneTryDistribution(maxToAward, roundState.getLeaderboard()))
                .addEvent(quizTimer)
                .build();

        quizTimer.addEventListener(round::checkEndOfRound);
        return round;
    }

    private static class ModifiedMaxGuess extends MaxGuess {

        public ModifiedMaxGuess(GuessCounter counter) {
            super(counter);
        }

        @Override
        public boolean existsEligible(Collection<? extends IDHolder<?>> players) {
            return true;
        }
    }
}
