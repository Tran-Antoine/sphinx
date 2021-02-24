package net.starype.quiz.api.game;

import net.starype.quiz.api.event.GameUpdatableHandler;
import net.starype.quiz.api.game.mock.MockPlayer;
import net.starype.quiz.api.game.mock.MockQuestion;
import net.starype.quiz.api.player.Player;
import net.starype.quiz.api.round.IndividualRoundFactory;
import net.starype.quiz.api.round.QuizRound;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static net.starype.quiz.api.game.ScoreDistribution.*;
import static net.starype.quiz.api.game.ScoreDistribution.EPSILON;

public class IndividualRoundTest {
    @Test
    public void round_ends_when_out_of_guesses() {
        QuizRound round = new IndividualRoundFactory().create(new MockQuestion(), 2);
        List<Player<UUID>> players = new ArrayList<>();
        players.add(new MockPlayer());
        players.add(new MockPlayer());

        Queue<QuizRound> rounds = new LinkedList<>();
        rounds.add(round);

        round.start(new SimpleGame<>(rounds, players),
                players, new GameUpdatableHandler());

        Assert.assertFalse(round.getEndingCondition().ends());
        round.onGuessReceived(players.get(0), "correct");
        Assert.assertFalse(round.getEndingCondition().ends());
        round.onGuessReceived(players.get(1), "kinda-correct");
        Assert.assertTrue(round.getEndingCondition().ends());
    }

    @Test
    public void score_correctly_distributed() {
        QuizRound round = new IndividualRoundFactory().create(new MockQuestion(), 10);
        List<Player<UUID>> players = new ArrayList<>();
        players.add(new MockPlayer());
        players.add(new MockPlayer());
        players.add(new MockPlayer());

        Queue<QuizRound> rounds = new LinkedList<>();
        rounds.add(round);

        round.start(new SimpleGame<>(rounds, players),
                players, new GameUpdatableHandler());

        round.onGuessReceived(players.get(0), "kinda-correct");
        round.onGuessReceived(players.get(1), "correct");
        round.onGuessReceived(players.get(2), "pretty-correct");

        ScoreDistribution scoreDistribution = round.getScoreDistribution();
        List<Standing> standings = scoreDistribution.applyAll(players, (player, score) -> {});

        Assert.assertEquals(standings.get(0).getPlayer(), players.get(1));
        Assert.assertEquals(standings.get(1).getPlayer(), players.get(2));
        Assert.assertEquals(standings.get(2).getPlayer(), players.get(0));

        Assert.assertEquals(10, standings.get(0).getScoreAcquired(), EPSILON);
        Assert.assertEquals(6.6, standings.get(1).getScoreAcquired(), EPSILON);
        Assert.assertEquals(3.3, standings.get(2).getScoreAcquired(), EPSILON);
    }

    @Test
    public void round_ends_when_players_give_up() {
        QuizRound round = new IndividualRoundFactory().create(new MockQuestion(), 10);
        List<Player<UUID>> players = new ArrayList<>();
        players.add(new MockPlayer());
        players.add(new MockPlayer());
        players.add(new MockPlayer());

        Queue<QuizRound> rounds = new LinkedList<>();
        rounds.add(round);

        round.start(new SimpleGame<>(rounds, players),
                players, new GameUpdatableHandler());

        Assert.assertFalse(round.getEndingCondition().ends());
        players.forEach(round::onGiveUpReceived);
        Assert.assertTrue(round.getEndingCondition().ends());

        ScoreDistribution scoreDistribution = round.getScoreDistribution();
        List<Standing> standings = scoreDistribution.applyAll(players, (player, aDouble) -> {});
        standings.forEach(s -> Assert.assertEquals(0.0, s.getScoreAcquired(), EPSILON));
    }
}
