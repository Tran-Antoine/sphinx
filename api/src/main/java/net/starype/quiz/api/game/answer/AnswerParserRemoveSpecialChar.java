package net.starype.quiz.api.game.answer;

public class AnswerParserRemoveSpecialChar implements AnswerParser {

    @Override
    public Answer process(Answer str) {
        String outputAnswer = str.getAnswerText()
                .replaceAll("[^a-zA-Z0-9,. ]", "");
        return Answer.fromString(outputAnswer);
    }
}
