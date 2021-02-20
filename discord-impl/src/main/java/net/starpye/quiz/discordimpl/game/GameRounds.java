package net.starpye.quiz.discordimpl.game;

import net.starype.quiz.api.game.ClassicalRoundFactory;
import net.starype.quiz.api.game.QuizRound;
import net.starype.quiz.api.game.IndividualRoundFactory;
import net.starype.quiz.api.game.question.Question;

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
