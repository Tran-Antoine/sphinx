package net.starype.quiz.api.game.answer;

public class RemoveSpecialCharProcessor implements AnswerProcessor {

    @Override
    public Answer process(Answer answer) {
        String outputAnswer = answer.getAnswerText()
                .replaceAll("[^a-zA-Z0-9,. ]", "");
        return Answer.fromString(outputAnswer);
    }
}
