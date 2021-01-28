package net.starype.quiz.api.game.question;

public enum QuestionDifficulty {
    EASY("EASY"),
    NORMAL("NORMAL"),
    HARD("HARD"),
    INSANE("INSANE");

    private final String name;

    QuestionDifficulty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
