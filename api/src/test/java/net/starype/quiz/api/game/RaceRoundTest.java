package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.UUIDHolder;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class RaceRoundTest {

    @Test
    public void round_ends_when_out_of_guesses() {
        Set<UUIDHolder> players = new HashSet<>();
        players.add(new MockUUIDHolder());
        players.add(new MockUUIDHolder());

        GameRound round = new RaceRound.Builder()
                .withMaxGuessesPerPlayer(1)
                .withQuestion(new MockQuestion())
                .withPointsToAward(1)
                .withPlayers(players)
                .build();

        round.init();
        GameRoundContext context = round.createContext();

        for(UUIDHolder player : players) {
            round.onGuessReceived(player, "INCORRECT ANSWER");
        }

        Assert.assertTrue(context.getEndingCondition().ends());
    }

    @Test
    public void round_ends_when_one_winner() {
        UUIDHolder player = new MockUUIDHolder();

        GameRound round = new RaceRound.Builder()
                .withMaxGuessesPerPlayer(3)
                .withQuestion(new MockQuestion())
                .withPointsToAward(1)
                .withPlayers(Collections.singletonList(player))
                .build();
        round.init();
        RoundEndingPredicate endingPredicate = round.createContext().getEndingCondition();

        Assert.assertFalse(endingPredicate.ends());
        round.onGuessReceived(player, "INCORRECT ANSWER");
        Assert.assertFalse(endingPredicate.ends());
        round.onGuessReceived(player, "CORRECT");
        Assert.assertTrue(endingPredicate.ends());
    }

    @Test
    public void score_was_awarded() {
        double pointsToAward = 3.5;
        Player player1 = new MockPlayer(UUID.randomUUID());
        Player player2 = new MockPlayer(UUID.randomUUID());

        GameRound round = new RaceRound.Builder()
                .withMaxGuessesPerPlayer(1)
                .withQuestion(new MockQuestion())
                .withPointsToAward(pointsToAward)
                .withPlayers(Arrays.asList(player1, player2))
                .build();

        round.init();
        round.onGuessReceived(player1, "CORRECT");
        ScoreDistribution scoreDistribution = round.createContext().getScoreDistributionCreator();

        double score1 = scoreDistribution.apply(player1);
        double score2 = scoreDistribution.apply(player2);

        Assert.assertEquals(score1, pointsToAward, 0.01);
        Assert.assertEquals(score2, 0, 0.01);
    }

    private static class MockUUIDHolder implements UUIDHolder {
        private UUID id = UUID.randomUUID();
        @Override
        public UUID getUUID() {
            return id;
        }
    }

    private static class MockPlayer extends Player {

        public MockPlayer(UUID uuid) {
            super(uuid, "");
        }
    }

    private static class MockQuestion implements Question {

        @Override
        public double submitAnswer(String answer) {
            if(answer.equals("CORRECT")) {
                return 1;
            }
            return 0;
        }

        @Override
        public Set<QuestionTag> getTags() { return null; }

        @Override
        public void registerTag(QuestionTag tag) { }

        @Override
        public void unregisterTag(QuestionTag tag) { }

        @Override
        public QuestionDifficulty getDifficulty() { return null; }

        @Override
        public String getRawQuestion() { return null; }

        @Override
        public String getDisplayableCorrectAnswer() { return null; }

        @Override
        public UUID getUUID() { return null; }
    }
}
