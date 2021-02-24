package net.starype.quiz.api.game;

import net.starype.quiz.api.game.mock.MockPlayer;
import net.starype.quiz.api.game.mock.MockServer;
import net.starype.quiz.api.player.Player;

import java.util.Scanner;

public class RaceGameSample {

    public static void main(String... args) {

        Player me = new MockPlayer();
        MockServer server = new MockServer();
        QuizGame game = GameFactory.createRaceGame(me, server);

        game.start();

        Scanner scanner = new Scanner(System.in);

        while(!server.isGameOver()) {
            System.out.println("Listening to inputs:");
            String answer = scanner.nextLine();
            game.sendInput(me, answer);
            game.update();
        }

        scanner.close();
    }

}
