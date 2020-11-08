package net.starype.quiz.api.game.answer;

public class Answer {

    private String answer;

    private Answer(String answer) {
        this.answer = processString(answer);
    }

    public static Answer fromString(String answer) {
        return new Answer(answer);
    }

    private String processString(String rawAnswer) {
        return rawAnswer
                .strip()
                .toLowerCase();
    }

    public String getAnswerText() {
        return answer;
    }

    public int asInt() {
        return Integer.parseInt(answer);
    }
}
