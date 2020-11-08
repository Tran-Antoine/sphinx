package net.starype.quiz.api.game.answer;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer1 = (Answer) o;
        return answer.equals(answer1.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answer);
    }
}
