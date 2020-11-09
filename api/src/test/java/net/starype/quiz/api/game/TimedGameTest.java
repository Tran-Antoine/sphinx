package net.starype.quiz.api.game;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class TimedGameTest {

    public static void main(String[] args) throws InterruptedException {
        MockServer server = new MockServer();
        QuizGame game = new SimpleGame(
                new LinkedList<>(Collections.singletonList(
                        new TimedRaceRound(1, new MockQuestion(), 1, 10, TimeUnit.SECONDS))),
                Collections.singletonList(new MockPlayer()),
                server);
        game.start();
        System.out.println("Game successfully started");
        while(!server.isGameOver()) {
            System.out.println("Waiting...");
            Thread.sleep(1000);
        }

        System.out.println("Game stopped because of time out!");
    }
}
