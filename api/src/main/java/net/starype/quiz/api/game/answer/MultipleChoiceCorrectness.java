package net.starype.quiz.api.game.answer;

import net.starype.quiz.api.game.util.MathUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultipleChoiceCorrectness implements CorrectnessEvaluator {

    private Set<String> acceptedAnswer;
    private LossFunction lossFunction;
    private double punitiveRatio = 1.0F;

    public MultipleChoiceCorrectness(Set<Answer> acceptedAnswer,
                                     LossFunction lossFunction,
                                     double punitiveRatio)
    {
        this.acceptedAnswer = acceptedAnswer
                .stream()
                .map(s -> s.getAnswerText().toLowerCase())
                .collect(Collectors.toSet());
        this.lossFunction = lossFunction;
        this.punitiveRatio = Math.max(punitiveRatio, 0.0);
    }

    public MultipleChoiceCorrectness(Set<Answer> acceptedAnswer, LossFunction lossFunction) {
        this(acceptedAnswer, lossFunction, 1.0);
    }

    @Override
    public double getCorrectness(Answer answer) {
        Set<String> stringSet = Arrays.asList(answer.getAnswerText()
                .split(";"))
                .stream()
                .map(s -> s.toLowerCase())
                .collect(Collectors.toSet());
        long goodGuess = stringSet
                .stream()
                .filter(s -> acceptedAnswer.contains(s))
                .count();
        long badGuess = stringSet.size() - goodGuess;
        double inverseCorrectness = ((double) goodGuess - punitiveRatio * (double) (badGuess)) / (double)acceptedAnswer.size();
        double correcness = MathUtils.clamp01(1.0 - inverseCorrectness);
        return lossFunction.evaluate(correcness);
    }
}
