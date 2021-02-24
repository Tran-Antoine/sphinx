package net.starype.quiz.api.game;

import net.starype.quiz.api.game.mock.MockPlayer;
import net.starype.quiz.api.game.mock.MockServer;
import net.starype.quiz.api.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ClassicalGameSample {

    public static void main(String[] args) {
        MockServer server = new MockServer();
        List<Player<?>> players = Arrays.asList(
                new MockPlayer(),
                new MockPlayer(),
                new MockPlayer()
        );
        QuizGame game = GameFactory.createClassicalGame(players, server);
        game.start();
        Scanner scanner = new Scanner(System.in);
        while(!server.isGameOver()) {
            System.out.println("Listening to inputs:");
            String input = scanner.nextLine();
            String[] inputArgs = input.split(" ");
            game.onInputReceived(players.get(Integer.parseInt(inputArgs[0])), inputArgs[1]);
            game.update();
        }
    }
}
