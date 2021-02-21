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

import static net.starype.quiz.api.game.ScoreDistribution.EPSILON;
import static net.starype.quiz.api.game.ScoreDistribution.Standing;

public class ClassicalRoundTest {

    private static CorrectAnswerFactory factory = new WordAnswerFactory();

    @Test
    public void round_ends_when_out_of_guesses() {
        UpdatableHandler updatableHandler = new GameUpdatableHandler();
        Set<Player<UUID>> players = new HashSet<>();
        players.add(new MockPlayer());
        players.add(new MockPlayer());

        QuizRound round = new ClassicalRoundFactory()
                .create(new MockQuestion(), 3, 1);

        Queue<QuizRound> rounds = new LinkedList<>();
        rounds.add(round);

        round.start(new SimpleGame<>(rounds, players), players, updatableHandler);

        for(Player<?> player : players) {
            round.onGuessReceived(player, "incorrect");
        }

        Assert.assertTrue(round.hasRoundEnded());
    }

    @Test
    public void round_ends_when_players_give_up() {
        UpdatableHandler updatableHandler = new GameUpdatableHandler();
        Set<Player<UUID>> players = new HashSet<>();
        players.add(new MockPlayer());
        players.add(new MockPlayer());

        QuizRound round = new ClassicalRoundFactory()
                .create(new MockQuestion(), 3, 1);

        Queue<QuizRound> rounds = new LinkedList<>();
        rounds.add(round);

        round.start(new SimpleGame<>(rounds, players), players, updatableHandler);

        for(Player<?> player : players) {
            round.onGiveUpReceived(player);
        }

        Assert.assertTrue(round.hasRoundEnded());
    }

    @Test
    public void score_correctly_distributed() {
        UpdatableHandler updatableHandler = new GameUpdatableHandler();

        Player<UUID> player1 = new MockPlayer();
        Player<UUID> player2 = new MockPlayer();
        Player<UUID> player3 = new MockPlayer();
        Player<UUID> player4 = new MockPlayer();
        Player<UUID> player5 = new MockPlayer();

        List<Player<UUID>> players = Arrays.asList(player1, player2, player3, player4, player5);

        QuizRound round = new ClassicalRoundFactory()
                .create(new MockQuestion(), 4, 1);

        Queue<QuizRound> rounds = new LinkedList<>();
        rounds.add(round);

        round.start(new SimpleGame<>(rounds, players), players, updatableHandler);

        round.onGuessReceived(player1, "correct");
        round.onGuessReceived(player2, "kinda-correct");
        round.onGuessReceived(player3, "pretty-correct");

        ScoreDistribution scoreDistribution = round.getScoreDistribution();

        List<Standing> standings = scoreDistribution.applyAll(players, (player, score) -> {});

        double score1 = standings.get(0).getScoreAcquired();
        double score2 = standings.get(1).getScoreAcquired();
        double score3 = standings.get(2).getScoreAcquired();
        double score4 = standings.get(3).getScoreAcquired();
        double score5 = standings.get(4).getScoreAcquired();

        Assert.assertEquals(player1, standings.get(0).getPlayer());
        Assert.assertEquals(player3, standings.get(1).getPlayer());
        Assert.assertEquals(player2, standings.get(2).getPlayer());
        Assert.assertEquals(player4, standings.get(3).getPlayer());
        Assert.assertEquals(player5, standings.get(4).getPlayer());

        Assert.assertEquals(4, score1, EPSILON);
        Assert.assertEquals(3, score2, EPSILON);
        Assert.assertEquals(2, score3, EPSILON);
        Assert.assertEquals(0, score4, EPSILON);
        Assert.assertEquals(0, score5, EPSILON);
    }
}
