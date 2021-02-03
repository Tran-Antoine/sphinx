package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;

import java.util.Collection;
import java.util.Map;

public interface GameRound {

    void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler);
    PlayerGuessContext onGuessReceived(Player<?> source, String message);
    void onGiveUpReceived(Player<?> source);
    default void onRoundStopped(){}

    //TODO : Return a list of EntityEligibility, and ScoreDistribution
    EntityEligibility initPlayerEligibility();
    RoundEndingPredicate initGuessEndingCondition();
    RoundEndingPredicate initTimeEndingCondition();
    ScoreDistribution initScoreDistribution();
    GameRoundReport initReport(Map<Player<?>, Double> standings);

    GameRoundContext getContext();
}
