package net.starype.quiz.api.database;

import net.starype.quiz.api.game.question.QuestionDifficulty;

public final class QuestionQueries {

    private QuestionQueries() {}

    public static final QuestionQuery ALL = (data) -> true;
    public static final QuestionQuery NONE = (data) -> false;

    public static QuestionQuery allFromDirectory(String path) {
        return (data) -> data.getFile().startsWith(path);
    }

    public static QuestionQuery allWithTag(String tag) {
        return (data) -> data.getTags().contains(tag);
    }

    public static QuestionQuery allWithKeyword(String word) {
        return (data) -> data.getText().contains(word);
    }

    public static QuestionQuery allWithDifficulty(QuestionDifficulty difficulty) { return  (data) -> data.getDifficulty().equals(difficulty.name()); }
}
