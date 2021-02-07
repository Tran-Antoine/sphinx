package net.starype.quiz.api.game;

import net.starype.quiz.api.DefaultSimpleGame;
import net.starype.quiz.api.game.mock.MockQuestion;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.server.GameServer;

import java.util.*;

public class GameFactory {

    public static QuizGame createRaceGame(Player<?> player, GameServer<? super QuizGame> server) {

        StandardRound raceRound = new RaceRoundFactory().create(new MockQuestion(), 2, 1.5);
        StandardRound raceRound2 = new RaceRoundFactory().create(new MockQuestion(), 2, 1.5);

        Queue<GameRound> rounds = new LinkedList<>(Arrays.asList(raceRound, raceRound2));

        return new DefaultSimpleGame(rounds, Collections.singletonList(player), server);
    }

    public static QuizGame createClassicalGame(List<Player<?>> players, GameServer<? super QuizGame> server) {
        GameRound round = new ClassicalRoundFactory().create(new MockQuestion(), 4.0, 5);
        Queue<GameRound> rounds = new LinkedList<>(Collections.singletonList(round));
        return new DefaultSimpleGame(rounds, players, server);
    }
}
