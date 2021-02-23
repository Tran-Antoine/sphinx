package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.GameUpdatableHandler;
import net.starype.quiz.api.game.mock.MockPlayer;
import net.starype.quiz.api.game.mock.MockQuestion;
import net.starype.quiz.api.game.player.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PollRoundTest {

    @Test
    public void round_ends_when_out_of_guesses() {
        QuizRound round = new PollRoundFactory().create(new MockQuestion(), 1);
        Queue<QuizRound> rounds = new LinkedList<>();
        rounds.add(round);

        List<Player<?>> players = new ArrayList<>();
        players.add(new MockPlayer());
        players.add(new MockPlayer());

        round.start(new SimpleGame<>(rounds, players), players, new GameUpdatableHandler());

        Assert.assertFalse(round.getEndingCondition().ends());
        players.forEach(player -> round.onGuessReceived(player, "an opinion"));
        Assert.assertTrue(round.getEndingCondition().ends());
    }

}
