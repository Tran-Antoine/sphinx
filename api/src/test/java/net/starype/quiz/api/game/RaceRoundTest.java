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
                .withQuestion(new MockQuestion())
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
                .withQuestion(new MockQuestion())
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

}
