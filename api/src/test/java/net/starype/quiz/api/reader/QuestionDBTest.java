package net.starype.quiz.api.reader;

import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.parser.QuestionDatabase;
import net.starype.quiz.api.util.StringUtils;

import java.util.List;

public class QuestionDBTest {
    public static void main(String[] args) {
        QuestionDatabase db = new QuestionDatabase("api/src/main/resources/questions", "db.bin",
                false, false);
        db.sync();
        List<Question> lQ = db.queryAll((tags, text, difficulty, file) -> tags.contains("Math"));
    }
}
