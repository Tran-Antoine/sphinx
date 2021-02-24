package net.starype.quiz.api.answer;

/**
 * Processor that removes trailing spaces and duplicated spaces.
 */
public class CleanStringProcessor implements AnswerProcessor {
    @Override
    public Answer process(Answer answer) {
        String outputAnswer = answer.getAnswerText()
                .strip()
                .replaceAll("\\s+", " ");
        return Answer.fromString(outputAnswer);
    }
}
