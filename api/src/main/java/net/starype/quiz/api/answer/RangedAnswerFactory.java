package net.starype.quiz.api.answer;

public interface RangedAnswerFactory extends CorrectAnswerFactory {

    RangedAnswerFactory withAcceptedRange(Number range);
    RangedAnswerFactory withInterpolation(LossFunction lossFunction);

}
