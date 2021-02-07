package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.UpdatableHandler;
import net.starype.quiz.api.game.player.Player;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.starype.quiz.api.game.ScoreDistribution.Standing;

public interface GameRound {

    void start(QuizGame game, Collection<? extends Player<?>> players, UpdatableHandler eventHandler,
               Consumer<GameRound> checkEndOfRound);

    PlayerGuessContext onGuessReceived(Player<?> source, String message);
    void onGiveUpReceived(Player<?> source);
    default void onRoundStopped(){}
    void checkEndOfRound();

    EntityEligibility initPlayerEligibility();
    RoundEndingPredicate initEndingCondition();
    ScoreDistribution initScoreDistribution();
    GameRoundReport initReport(List<Standing> standings);

    GameRoundContext getContext();
}
