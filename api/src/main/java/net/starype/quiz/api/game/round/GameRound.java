package net.starype.quiz.api.game.round;

import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.ScoreDistribution;
import net.starype.quiz.api.game.player.IDHolder;

import java.util.List;

public interface GameRound {

    boolean isEligible(IDHolder<?> holder);
    boolean hasRoundEnded();
    ScoreDistribution getScoreDistribution();
    GameRoundReport getReport(List<ScoreDistribution.Standing> standings);
}
