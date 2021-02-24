package helloquiz;

import net.starype.quiz.api.game.DefaultSimpleGame;
import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.api.game.SimpleGame;
import net.starype.quiz.api.player.Player;
import net.starype.quiz.api.question.Question;
import net.starype.quiz.api.round.ClassicalRoundFactory;
import net.starype.quiz.api.round.IndividualRoundFactory;
import net.starype.quiz.api.round.QuizRound;
import net.starype.quiz.api.server.GameServer;

import java.util.*;

public class HelloQuiz {

    public static QuizGame createGame(GameServer<? super SimpleGame<?>> server) {
        Question question = new MockQuestion();

        Queue<QuizRound> rounds = new LinkedList<>(Arrays.asList(
                new IndividualRoundFactory().create(question, 2),
                new ClassicalRoundFactory().create(question, 3, 2)
        ));

        Collection<Player<?>> players = Collections.singletonList(new MockPlayer("Antoine"));

        QuizGame game = new DefaultSimpleGame(rounds, players, server);
        return game;
    }
}
