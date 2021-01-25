package net.starype.quiz.api.parser;

public final class QuestionQueries {

    private QuestionQueries() {}

    public static final QuestionQuery ALL = (data) -> true;

    public static QuestionQuery allFromDirectory(String path) {
        return (data) -> data.getFile().startsWith(path);
    }

    public static QuestionQuery allWithTag(String tag) {
        return (data) -> data.getTags().contains(tag);
    }

    public static QuestionQuery allWithKeyword(String word) {
        return (data) -> data.getText().contains(word);
    }
}
