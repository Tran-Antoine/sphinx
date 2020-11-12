package net.starype.quiz.api.game.question;

public class QuestionTag {
    private String tag;

    public QuestionTag(String rawTag) {
        this.tag = formatTag(rawTag);
    }
    
    private String formatTag(String rawTag) {
        return rawTag
                .strip()
                .replaceAll(" ", "-")
                .replaceAll("[^a-zA-Z0-9 -]", "");
    }
    public String getTag() {
        return tag;
    }
}
