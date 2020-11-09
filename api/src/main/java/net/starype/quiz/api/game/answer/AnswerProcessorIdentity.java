package net.starype.quiz.api.game.answer;

public class AnswerProcessorIdentity implements AnswerProcessor {
    @Override
    public Answer process(Answer str) {
        return str;
    }
}
