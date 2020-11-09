package net.starype.quiz.api.game;

public class GameRoundContext {

    private EntityEligibility playerEligibility;
    private RoundEndingPredicate endingCondition;
    private ScoreDistribution scoreDistributionCreator;
    private GameRoundReport reportCreator;

    public GameRoundContext(GameRound round) {
        this.playerEligibility = round.initPlayerEligibility();
        this.endingCondition = round.initEndingCondition();
        this.scoreDistributionCreator = round.initScoreDistribution();
        this.reportCreator = round.initReport();
    }

    public EntityEligibility getPlayerEligibility() {
        return playerEligibility;
    }

    public GameRoundReport getReportCreator() {
        return reportCreator;
    }

    public RoundEndingPredicate getEndingCondition() {
        return endingCondition;
    }

    public ScoreDistribution getScoreDistribution() {
        return scoreDistributionCreator;
    }
}
