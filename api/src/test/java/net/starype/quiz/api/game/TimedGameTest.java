package net.starype.quiz.api.game;

import net.starype.quiz.api.game.mock.MockPlayer;
import net.starype.quiz.api.game.mock.MockQuestion;
import net.starype.quiz.api.game.mock.MockServer;
import net.starype.quiz.api.round.QuizRound;
import net.starype.quiz.api.round.TimedRaceRoundFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class TimedGameTest {

    public static void main(String[] args) throws InterruptedException {
        MockServer server = new MockServer();

        QuizRound timedRaceRound = new TimedRaceRoundFactory().create(new MockQuestion(),
                1, 1, 10, TimeUnit.SECONDS);

        SimpleGame<QuizGame> game = new SimpleGame<>(
                new LinkedList<>(Collections.singletonList(timedRaceRound)),
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
