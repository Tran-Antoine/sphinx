package net.starype.quiz.api.reader;

import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.parser.QuestionDatabase;

public class QuestionDBTest {
    public static void main(String[] args) {
        // new File("api/src/test/non-compiled-resources/reader/db.bin").delete();
        QuestionDatabase db = new QuestionDatabase("",
                "api/src/test/non-compiled-resources/reader/db.bin", true, true);
        db.sync();
        Question q = db.pickQuery(arguments -> true).orElseThrow();
        System.out.println(q.getId());
    }
}
