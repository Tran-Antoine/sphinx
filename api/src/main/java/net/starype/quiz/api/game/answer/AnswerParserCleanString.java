package net.starype.quiz.api.game.answer;

public class AnswerParserCleanString implements AnswerParser {
    @Override
    public Answer process(Answer answer) {
        String outputAnswer = answer.getAnswerText()
                .strip()
                .replaceAll("( )+", " ");
        return Answer.fromString(outputAnswer);
    }
}
