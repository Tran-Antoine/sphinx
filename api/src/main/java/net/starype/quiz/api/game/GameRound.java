package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.Collection;

public interface GameRound {

    void init(QuizGame game, Collection<? extends UUIDHolder> players);
    void onGuessReceived(UUIDHolder source, String message);
    void onGiveUpReceived(UUIDHolder source);

    EntityEligibility initPlayerEligibility();
    RoundEndingPredicate initEndingCondition();
    ScoreDistribution initScoreDistribution();
    GameRoundReport initReport();

    GameRoundContext getContext();
}
