package net.starype.quiz.api.game.answer;

public class AnswerProcessorRemoveSpecialChar implements AnswerProcessor {

    @Override
    public Answer process(Answer str) {
        String outputAnswer = str.getAnswerText()
                .replaceAll("[^a-zA-Z0-9,. ]", "");
        return Answer.fromString(outputAnswer);
    }
}
