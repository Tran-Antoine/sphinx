package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.server.GameServer;

import java.util.*;

public class SimpleGameTest {

    public static void main(String... args) {

        Player me = new MockPlayer();
        MockServer server = new MockServer();
        QuizGame game = createGame(me, server);

        game.start();

        Scanner scanner = new Scanner(System.in);

        while(!server.isGameOver()) {
            System.out.println("Listening to inputs:");
            String answer = scanner.nextLine();
            game.onInputReceived(me, answer);
            game.update();
        }

        scanner.close();
    }

    private static QuizGame createGame(Player player, GameServer server) {
        RaceRound.Builder builder = new RaceRound.Builder()
                .withQuestion(new MockQuestion())
                .withMaxGuessesPerPlayer(2)
                .withPointsToAward(1.5);

        Queue<GameRound> rounds = new LinkedList<>(Arrays.asList(builder.build(), builder.build()));

        return new SimpleGame(rounds, Collections.singletonList(player), server);
    }
}
