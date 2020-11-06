package net.starype.quiz.api.game;

public class GameRoundContext {

    private AnswerEligibility playerEligibility;
    private RoundEndingPredicate endingCondition;
    private ScoreDistribution scoreDistributionCreator;
    private GameRoundReport reportCreator;

    GameRoundContext(GameRound round) {
        this.playerEligibility = round.playerEligibility();
        this.endingCondition = round.endingCondition();
        this.scoreDistributionCreator = round.createScoreDistribution();
        this.reportCreator = round.createReport();
    }

    public AnswerEligibility getPlayerEligibility() {
        return playerEligibility;
    }

    public GameRoundReport getReportCreator() {
        return reportCreator;
    }

    public RoundEndingPredicate getEndingCondition() {
        return endingCondition;
    }

    public ScoreDistribution getScoreDistributionCreator() {
        return scoreDistributionCreator;
    }
}
