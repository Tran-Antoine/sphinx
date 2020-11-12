package net.starype.quiz.api.game;

public class GameRoundContext {

    private EntityEligibility playerEligibility;
    private RoundEndingPredicate endingCondition;
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
            reportCreator = round.initReport();
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
