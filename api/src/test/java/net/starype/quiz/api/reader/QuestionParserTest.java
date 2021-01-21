package net.starype.quiz.api.reader;

import net.starype.quiz.api.parser.QuestionParser;
import org.junit.Test;

import java.io.IOException;

public class QuestionParserTest {

    @Test
    public void get_section() {
        try {
            System.out.println(QuestionParser.parseTOML("src/main/resources/questions/group1/q2.toml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
