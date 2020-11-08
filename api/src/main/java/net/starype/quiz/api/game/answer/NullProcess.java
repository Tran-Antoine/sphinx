package net.starype.quiz.api.game.answer;

public class NullProcess implements AnswerParser {
    @Override
    public Answer process(Answer str) {
        return str;
    }
}
