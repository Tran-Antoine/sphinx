package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.game.event.UpdatableHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;

import java.util.Collection;
import java.util.List;

public interface GameRound {

    void start(QuizGame game, Collection<? extends IDHolder<?>> players, UpdatableHandler updatableHandler);
    PlayerGuessContext onGuessReceived(Player<?> source, String message);
    void onGiveUpReceived(IDHolder<?> source);
    default void onRoundStopped(){}

    EntityEligibility initPlayerEligibility();
    RoundEndingPredicate initEndingCondition();
    ScoreDistribution initScoreDistribution();
    GameRoundReport initReport(List<Standing> standings);

    GameRoundContext getContext();
}
