package net.starype.quiz.api.reader;

import net.starype.quiz.api.database.QuestionQueries;
import net.starype.quiz.api.database.QuizQueryable;
import net.starype.quiz.api.database.QuestionDatabases;
import net.starype.quiz.api.question.Question;

import java.util.List;

public class QuestionDBTest {
    public static void main(String[] args) {
        QuizQueryable queryTool = QuestionDatabases.fromLocalPath(
                "api/src/test/resources/questions",
                "api/src/test/resources/db.sphinx",
                false,
                false);
        List<Question> questions = queryTool.listQuery(QuestionQueries.allFromDirectory("test"));
        System.out.println(questions);
    }
}
