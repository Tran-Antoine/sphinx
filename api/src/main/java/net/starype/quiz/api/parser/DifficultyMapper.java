package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableMap;
import net.starype.quiz.api.game.question.QuestionDifficulty;

public class DifficultyMapper implements ConfigMapper<QuestionDifficulty> {

    private String name;
    private QuestionDifficulty difficulty;

    public DifficultyMapper(String name, QuestionDifficulty difficulty) {
        this.name = name;
        this.difficulty = difficulty;
    }

    @Override
    public String getMapperName() {
        return name;
    }

    @Override
    public QuestionDifficulty map(ReadableMap config) {
        return difficulty;
    }
}
