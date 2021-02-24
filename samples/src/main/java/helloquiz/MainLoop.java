package helloquiz;

import net.starype.quiz.api.game.QuizGame;

import java.util.Scanner;

public class MainLoop {

    public static void main(String[] args) {

        HelloServer server = new HelloServer();
        QuizGame game = HelloQuiz.createGame(server);

        Scanner scanner = new Scanner(System.in);

        game.start();
        while(!server.isGameOver()) {
            String[] inputArgs = scanner.nextLine().split(" ");

            if(inputArgs.length != 2) {
                System.out.println("Syntax: <playerId> <message>");
                continue;
            }

            String playerId = inputArgs[0];
            String message = inputArgs[1];

            try {
                game.onInputReceived(playerId, message);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
