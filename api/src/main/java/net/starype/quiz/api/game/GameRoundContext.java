package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

import java.util.Map;

public class GameRoundContext {

    private EntityEligibility playerEligibility;
    private RoundEndingPredicate endingCondition;
    private RoundEndingPredicate timeEndingCondition;
    private ScoreDistribution scoreDistributionCreator;
    private GameRoundReport reportCreator;

    private GameRound round;

    public GameRoundContext(GameRound round) {
        this.round = round;
    }

    public EntityEligibility getPlayerEligibility() {
        if(playerEligibility == null) {
            playerEligibility = round.initPlayerEligibility();
        }
        return playerEligibility;
    }

    public GameRoundReport getReportCreator() {
        if(reportCreator == null) {
            throw new IllegalStateException("Can't retrieve the report after it has been initialized with the standings");
        }
        return reportCreator;
    }

    public GameRoundReport getReportCreator(Map<Player<?>, Double> standings) {
        if (reportCreator == null) {
            this.reportCreator = round.initReport(standings);
        }
        return reportCreator;
    }

    public RoundEndingPredicate getEndingCondition() {
        if(endingCondition == null) {
            endingCondition = round.initEndingCondition();
        }
        return endingCondition;
    }

    public ScoreDistribution getScoreDistribution() {
        if(scoreDistributionCreator == null) {
            scoreDistributionCreator = round.initScoreDistribution();
        }
        return scoreDistributionCreator;
    }
}
