package net.starype.quiz.api.game.answer;

import java.util.Arrays;

public class AnswerProcessorCleanSeparator implements AnswerProcessor {
    @Override
    public Answer process(Answer answer) {
        String parsedAnswer = answer.getAnswerText()
                .strip()
                .replace(",", ";")
                .replace(" ", ";")
                .replace("-", ";")
                .replace("|", ";")
                .replaceAll("[\\;]+", ";");
        return Answer.fromString(parsedAnswer);
    }
}
