package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface GameRound {

    void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler);
    PlayerGuessContext onGuessReceived(Player<?> source, String message);
    void onGiveUpReceived(IDHolder<?> source);
    default void onRoundStopped(){}

    EntityEligibility initPlayerEligibility();
    RoundEndingPredicate initEndingCondition();
    ScoreDistribution initScoreDistribution();
    GameRoundReport initReport(List<Standing> standings);

    GameRoundContext getContext();
}
