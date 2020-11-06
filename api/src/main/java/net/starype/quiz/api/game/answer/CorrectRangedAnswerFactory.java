package net.starype.quiz.api.game.answer;

public interface CorrectRangedAnswerFactory extends CorrectAnswerFactory {

    CorrectRangedAnswerFactory setAcceptedRange(Number range);

}
