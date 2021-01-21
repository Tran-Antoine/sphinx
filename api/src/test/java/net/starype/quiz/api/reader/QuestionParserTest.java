package net.starype.quiz.api.reader;

import net.starype.quiz.api.parser.QuestionParser;
import org.junit.Test;

import java.io.IOException;

public class QuestionParserTest {

    @Test
    public void get_section() {
        try {
            QuestionParser.parse("src/main/resources/questions/algebra/q1.toml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
