package net.starype.quiz.api.game.answer;

public class IdentityProcessor implements AnswerProcessor {

    public static final AnswerProcessor INSTANCE = new IdentityProcessor();

    @Override
    public Answer process(Answer answer) {
        return answer;
    }
}
