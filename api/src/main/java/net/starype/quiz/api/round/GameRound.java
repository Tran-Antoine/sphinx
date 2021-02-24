package net.starype.quiz.api.round;

import net.starype.quiz.api.game.EndingPredicate;
import net.starype.quiz.api.game.EntityEligibility;
import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.ScoreDistribution;

import java.util.List;

public interface GameRound {

    EntityEligibility getPlayerEligibility();
    EndingPredicate getEndingCondition();
    ScoreDistribution getScoreDistribution();
    GameRoundReport getReport(List<ScoreDistribution.Standing> standings);
}
