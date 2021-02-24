package net.starype.quiz.discordimpl.game;

import net.starype.quiz.api.round.ClassicalRoundFactory;
import net.starype.quiz.api.round.QuizRound;
import net.starype.quiz.api.round.IndividualRoundFactory;
import net.starype.quiz.api.question.Question;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class GameRounds {
    public static Queue<? extends QuizRound> defaultPreset(Question q1, Question q2) {
        return new LinkedList<>(Arrays.asList(
                new ClassicalRoundFactory().create(q1, 3, 1),
                new IndividualRoundFactory().create(q2, 2)));
    }
}
