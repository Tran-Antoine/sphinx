package net.starype.quiz.api.game;

public interface SettableRound {
    void addScoreDistribution(ScoreDistribution scoreDistribution);
    void withEndingCondition(RoundEndingPredicate roundEndingPredicate);
    void addPlayerEligibility(EntityEligibility entityEligibility);
}
