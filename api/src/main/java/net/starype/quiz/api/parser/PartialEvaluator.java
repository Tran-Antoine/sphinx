package net.starype.quiz.api.parser;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.answer.AnswerEvaluator;
import net.starype.quiz.api.game.answer.AnswerProcessor;

import java.util.Set;

public interface PartialEvaluator {

    AnswerEvaluator create(Set<Answer> answers, AnswerProcessor processor);
}
