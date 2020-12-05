package net.starype.quiz.api.game.answer;

public class CleanStringProcessor implements AnswerProcessor {
    @Override
    public Answer process(Answer answer) {
        String outputAnswer = answer.getAnswerText()
                //.strip()
                .replaceAll("\\s+", " ");
        return Answer.fromString(outputAnswer);
    }
}
