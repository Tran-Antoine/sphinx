package net.starype.quiz.api.game;

public class QuestionTag {
    private String tag;

    public QuestionTag(String tag) {
        this.tag = tag
                .strip()
                .replaceAll(" ", "-")
                .replaceAll("[^a-zA-Z0-9 -]", "");
    }

    String getTag() {
        return tag;
    }
}
