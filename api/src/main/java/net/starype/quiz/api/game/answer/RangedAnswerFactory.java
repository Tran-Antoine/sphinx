package net.starype.quiz.api.game.answer;

public interface RangedAnswerFactory extends CorrectAnswerFactory {

    RangedAnswerFactory withAcceptedRange(Number range);
    RangedAnswerFactory withInterpolation(LossFunction lossFunction);

}
