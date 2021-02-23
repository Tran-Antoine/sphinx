package net.starype.quiz.api.game;

import net.starype.quiz.api.game.mock.MockQuestion;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.server.GameServer;

import java.util.*;

public class GameFactory {

    public static QuizGame createRaceGame(Player<?> player, GameServer<? super QuizGame> server) {

        QuizRound raceRound = new RaceRoundFactory().create(new MockQuestion(), 2, 1.5);
        QuizRound raceRound2 = new RaceRoundFactory().create(new MockQuestion(), 2, 1.5);

        Queue<QuizRound> rounds = new LinkedList<>(Arrays.asList(raceRound, raceRound2));

        return new DefaultSimpleGame(rounds, Collections.singletonList(player), server);
    }

    public static QuizGame createClassicalGame(List<Player<?>> players, GameServer<? super QuizGame> server) {
        QuizRound round = new ClassicalRoundFactory().create(new MockQuestion(), 4.0, 5);
        Queue<QuizRound> rounds = new LinkedList<>(Collections.singletonList(round));
        return new DefaultSimpleGame(rounds, players, server);
    }
}
