package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.CorrectAnswerFactory;
import net.starype.quiz.api.game.answer.WordAnswerFactory;
import net.starype.quiz.api.game.event.GameUpdatableHandler;
import net.starype.quiz.api.game.event.UpdatableHandler;
import net.starype.quiz.api.game.mock.MockPlayer;
import net.starype.quiz.api.game.mock.MockQuestion;
import net.starype.quiz.api.game.player.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class ClassicalRoundTest {

    private static CorrectAnswerFactory factory = new WordAnswerFactory();

    @Test
    public void round_ends_when_out_of_guesses() {
        UpdatableHandler updatableHandler = new GameUpdatableHandler();
        Set<Player<UUID>> players = new HashSet<>();
        players.add(new MockPlayer());
        players.add(new MockPlayer());

        GameRound round = new ClassicalRoundFactory()
                .create(new MockQuestion(), 3, 1);

        round.start(null, players, updatableHandler, r -> {});
        GameRoundContext context = round.getContext();

        for(Player<?> player : players) {
            round.onGuessReceived(player, "incorrect");
        }

        Assert.assertTrue(context.getEndingCondition().ends());
    }

    public void round_ends_when_players_give_up() {
        UpdatableHandler updatableHandler = new GameUpdatableHandler();
        Set<Player<UUID>> players = new HashSet<>();
        players.add(new MockPlayer());
        players.add(new MockPlayer());

        GameRound round = new ClassicalRoundFactory()
                .create(new MockQuestion(), 3, 1);

        round.start(null, players, updatableHandler, r -> {});
        GameRoundContext context = round.getContext();

        for(Player<?> player : players) {
            round.onGiveUpReceived(player);
        }

        Assert.assertTrue(context.getEndingCondition().ends());
    }

    @Test
    public void score_correctly_distributed() {
        UpdatableHandler updatableHandler = new GameUpdatableHandler();

        Player<UUID> player1 = new MockPlayer();
        Player<UUID> player2 = new MockPlayer();
        Player<UUID> player3 = new MockPlayer();
        Player<UUID> player4 = new MockPlayer();

        List<Player<UUID>> players = Arrays.asList(player1, player2, player3, player4);

        GameRound round = new ClassicalRoundFactory()
                .create(new MockQuestion(), 3, 1);

        round.start(null, players, updatableHandler, r -> {});

        round.onGuessReceived(player1, "correct");
        round.onGuessReceived(player2, "kinda-correct");
        round.onGuessReceived(player3, "pretty-correct");
        round.onGuessReceived(player4, "correct");

        ScoreDistribution scoreDistribution = round.initScoreDistribution();

        double score1 = scoreDistribution.apply(player1);
        double score2 = scoreDistribution.apply(player2);
        double score3 = scoreDistribution.apply(player3);
        double score4 = scoreDistribution.apply(player4);

        Assert.assertEquals(3, score1, ScoreDistribution.EPSILON);
        Assert.assertEquals(0, score2, ScoreDistribution.EPSILON);
        Assert.assertEquals(1, score3, ScoreDistribution.EPSILON);
        Assert.assertEquals(2, score4, ScoreDistribution.EPSILON);

    }
}
