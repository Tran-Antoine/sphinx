package net.starype.quiz.api.game.answer;

public class LowercaseProcessor implements AnswerProcessor {
    @Override
    public Answer process(Answer answer) {
        return answer.mapText(String::toLowerCase);
    }
}
