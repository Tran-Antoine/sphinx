package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.*;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.UUIDHolder;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class RaceRoundTest {

    private static CorrectAnswerFactory factory = new WordCorrectAnswerFactory();

    @Test
    public void round_ends_when_out_of_guesses() {
        Set<UUIDHolder> players = new HashSet<>();
        players.add(new MockUUIDHolder());
        players.add(new MockUUIDHolder());

        GameRound round = new RaceRound.Builder()
                .withMaxGuessesPerPlayer(1)
                .withQuestion(new MockQuestion(factory.createCorrectAnswer(Answer.fromString("CORRECT"), new AnswerProcessorIdentity())))
                .build();

        round.init(null, players);
        GameRoundContext context = round.getContext();

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
                .withQuestion(new MockQuestion(factory.createCorrectAnswer(Answer.fromString("CORRECT"), new AnswerProcessorIdentity())))
                .build();
      
        round.init(null, Collections.singletonList(player));
        RoundEndingPredicate endingPredicate = round.getContext().getEndingCondition();

        Assert.assertFalse(endingPredicate.ends());
        round.onGuessReceived(player, "INCORRECT ANSWER");
        Assert.assertFalse(endingPredicate.ends());
        round.onGuessReceived(player, "CORRECT");
        Assert.assertTrue(endingPredicate.ends());
    }

    @Test
    public void game_ends_when_players_give_up() {
        Player player = new MockPlayer();
        GameRound round = new RaceRound.Builder()
                .withMaxGuessesPerPlayer(10)
                .build();
        round.init(null, Collections.singletonList(player));
        RoundEndingPredicate endingCondition = round.getContext().getEndingCondition();
        Assert.assertFalse(endingCondition.ends());
        round.onGiveUpReceived(player);
        Assert.assertTrue(endingCondition.ends());
    }

    @Test
    public void score_was_awarded() {
        double pointsToAward = 3.5;
        Player player1 = new MockPlayer();
        Player player2 = new MockPlayer();

        GameRound round = new RaceRound.Builder()
                .withMaxGuessesPerPlayer(1)
                .withQuestion(new MockQuestion(factory.createCorrectAnswer(Answer.fromString("CORRECT"), new AnswerProcessorIdentity())))
                .withPointsToAward(pointsToAward)
                .build();

        round.init(null, Arrays.asList(player1, player2));
        round.onGuessReceived(player1, "CORRECT");
        ScoreDistribution scoreDistribution = round.getContext().getScoreDistribution();

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

        public MockPlayer() {
            super(UUID.randomUUID(), "");
        }
    }

    private static class MockQuestion implements Question {

        private CorrectAnswer correctAnswer;

        public MockQuestion(CorrectAnswer correctAnswer) {
            this.correctAnswer = correctAnswer;
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

        @Override
        public Optional<Double> evaluateAnswer(Answer answer) {
            if(!correctAnswer.getValidityEvaluator()
                    .isValid(answer))
                return Optional.empty();
            return Optional.of(correctAnswer.getCorrectnessEvaluator()
                    .getCorrectness(answer));
        }
    }
}
