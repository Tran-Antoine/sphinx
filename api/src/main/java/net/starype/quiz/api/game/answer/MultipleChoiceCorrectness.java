package net.starype.quiz.api.game.answer;

import net.starype.quiz.api.util.MathUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class MultipleChoiceCorrectness implements CorrectnessEvaluator {

    private Set<String> acceptedAnswers;
    private LossFunction lossFunction;
    private double punitiveRatio = 1.0F;

    public MultipleChoiceCorrectness(Set<Answer> acceptedAnswers,
                                     LossFunction lossFunction,
                                     double punitiveRatio)
    {
        this.acceptedAnswers = acceptedAnswers
                .stream()
                .map(Answer::getAnswerText)
                .collect(Collectors.toSet());
        this.lossFunction = lossFunction;
        this.punitiveRatio = Math.max(punitiveRatio, 0.0);
    }

    public MultipleChoiceCorrectness(Set<Answer> acceptedAnswers, LossFunction lossFunction) {
        this(acceptedAnswers, lossFunction, 1.0);
    }

    @Override
    public double getCorrectness(Answer answer) {
        Set<String> stringSet = Set.of(answer.getAnswerText().split(";"));
        long goodGuess = stringSet
                .stream()
                .filter(acceptedAnswers::contains)
                .count();
        return evaluateCorrectness(goodGuess, stringSet.size() - goodGuess);
    }

    private double evaluateCorrectness(long goodGuess, long badGuess) {
        double score = (double) goodGuess - punitiveRatio * (double) (badGuess);
        double correctness =1.0 - MathUtils.clampedLerp01(
                score,
                0.0,
                acceptedAnswers.size());
        return lossFunction.evaluate(correctness);
    }
}
