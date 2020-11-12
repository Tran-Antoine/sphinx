package net.starype.quiz.api.game.answer;

import net.starype.quiz.api.game.util.MathUtils;

import java.util.Arrays;
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
                .map(s -> s.toString().toLowerCase())
                .collect(Collectors.toSet());
        this.lossFunction = lossFunction;
        this.punitiveRatio = Math.max(punitiveRatio, 0.0);
    }

    public MultipleChoiceCorrectness(Set<Answer> acceptedAnswer, LossFunction lossFunction) {
        this(acceptedAnswer, lossFunction, 1.0);
    }

    @Override
    public double getCorrectness(Answer answer) {
        Stream<String> stringStream = Arrays.asList(answer.getAnswerText()
                .replace(",", ";")
                .replace(" ", ";")
                .replace("-", ";")
                .replace("|", ";")
                .replaceAll("[\\;]+", ";")
                .split(";"))
                .stream()
                .map(s -> s.toLowerCase())
                .distinct();
        long goodGuess = stringStream
                .filter(s -> acceptedAnswer.contains(s))
                .count();
        long badGuess = stringStream.count() - goodGuess;
        return lossFunction.evaluate(goodGuess - punitiveRatio * badGuess,
                0.0,
                acceptedAnswer.size());
    }
}
