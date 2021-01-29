package net.starype.quiz.api.reader;

import net.starype.quiz.api.database.QuestionDatabases;
import net.starype.quiz.api.database.SimpleQuestionDatabase;

public class QuestionDBTest {
    public static void main(String[] args) {
        SimpleQuestionDatabase db = QuestionDatabases.fromLocalPath("api/src/test/resources/questions",
                "api/src/test/resources/db.sphinx", false, false);
        db.sync();
    }
}
