package net.starype.quiz.api.reader;

<<<<<<< HEAD
import net.starype.quiz.api.parser.QuestionDatabase;
import net.starype.quiz.api.database.QuestionDatabases;
import net.starype.quiz.api.parser.SimpleQuestionDatabase;
import net.starype.quiz.api.parser.QuestionQueries;
=======
>>>>>>> db-improvement-merge

public class QuestionDBTest {
    public static void main(String[] args) {
<<<<<<< HEAD
        QuestionDatabase db =  QuestionDatabases.fromLocalPath(
                "api/src/main/resources/questions",
                "db.bin",
                SimpleQuestionDatabase.TABLE,
                false,
                false);
=======
        SimpleQuestionDatabase db = SimpleQuestionDatabase
                .createDatabaseFromDirectory("api/src/test/resources/questions", "api/src/test/resources/db.bin",
                        false, true);
>>>>>>> db-improvement-merge
        db.sync();
    }
}
