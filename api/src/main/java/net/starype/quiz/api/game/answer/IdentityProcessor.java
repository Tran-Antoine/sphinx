package net.starype.quiz.api.game.answer;

public class IdentityProcessor implements AnswerProcessor {
    @Override
    public Answer process(Answer answer) {
        return answer;
    }
}
