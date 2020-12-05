package net.starype.quiz.api.game;

import net.starype.quiz.api.game.mock.MockPlayer;
import net.starype.quiz.api.game.mock.MockQuestion;
import net.starype.quiz.api.game.mock.MockServer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class TimedGameTest {

    public static void main(String[] args) throws InterruptedException {
        MockServer server = new MockServer();
        SimpleGame<QuizGame> game = new SimpleGame<>(
                new LinkedList<>(Collections.singletonList(
                        new TimedRaceRound(new MockQuestion(), 1, 1, 10, TimeUnit.SECONDS))),
                Collections.singletonList(new MockPlayer()));
        game.setGate(server.createGate());
        game.start();
        System.out.println("Game successfully started");
        while(!server.isGameOver()) {
            System.out.println("Waiting...");
            game.update();
            Thread.sleep(1000);
        }

        System.out.println("Game stopped because of time out!");
    }
}
