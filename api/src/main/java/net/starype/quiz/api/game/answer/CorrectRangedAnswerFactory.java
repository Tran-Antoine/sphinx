package net.starype.quiz.api.game.answer;

public interface CorrectRangedAnswerFactory extends CorrectAnswerFactory {

    CorrectRangedAnswerFactory withAcceptedRange(Number range);
    CorrectRangedAnswerFactory withInterpolation(LossFunction lossFunction);

}
