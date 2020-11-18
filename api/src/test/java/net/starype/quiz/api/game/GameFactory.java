package net.starype.quiz.api.game;

import net.starype.quiz.api.game.mock.MockQuestion;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.server.GameServer;

import java.util.*;

public class GameFactory {

    public static QuizGame createRaceGame(Player<?> player, GameServer server) {
        RaceRound.Builder builder = new RaceRound.Builder()
                .withQuestion(new MockQuestion())
                .withMaxGuessesPerPlayer(2)
                .withPointsToAward(1.5);

        Queue<GameRound> rounds = new LinkedList<>(Arrays.asList(builder.build(), builder.build()));

        return new SimpleGame(rounds, Collections.singletonList(player), server);
    }

    public static QuizGame createClassicalGame(List<Player<?>> players, GameServer server) {
        GameRound round = new ClassicalRound(new MockQuestion(), 5, 4.0);
        Queue<GameRound> rounds = new LinkedList<>(Collections.singletonList(round));
        return new SimpleGame(rounds, players, server);
    }
}
