package net.starype.quiz.api.game.question;

import net.starype.quiz.api.parser.QuestionParser;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class QuestionSet {

    private final List<? extends Question> questions;
    private final Random random = new Random();

    public QuestionSet(List<? extends Question> questions) {
        this.questions = questions;
    }

    public static QuestionSet fromDirectory(String path) {
        List<Question> questions = new ArrayList<>();
        File directory = new File(path);
        File[] files = directory.listFiles();

        if(files == null) {
            throw new IllegalArgumentException("Given path is not a directory");
        }

        for(File file : files) {
            if(!file.isFile()) {
                continue;
            }
            try {
                questions.add(QuestionParser.parseTOML(file));
            } catch (IOException ignored) { }
        }
        return new QuestionSet(questions);
    }

    public static QuestionSet fromTags(String... tags) {
        throw new IllegalStateException("Not implemented yet");
    }

    public static QuestionSet fromSearch(String search) {
        throw new IllegalStateException("Not implemented yet");
    }

    public Question drawRandom() {
        return questions.get(random.nextInt(questions.size()));
    }

    public Question popRandom() {
        return questions.remove(random.nextInt(questions.size()));
    }
}
