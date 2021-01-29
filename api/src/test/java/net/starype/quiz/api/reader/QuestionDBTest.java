package net.starype.quiz.api.reader;

import net.starype.quiz.api.database.Serializer;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.database.SimpleQuestionDatabase;
import net.starype.quiz.api.database.QuestionQueries;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuestionDBTest {
    public static void main(String[] args) {
        SimpleQuestionDatabase db = SimpleQuestionDatabase
                .createDatabaseFromDirectory("api/src/test/resources/questions", "api/src/test/resources/db.bin",
                        false, true);
        db.sync();
    }
}
