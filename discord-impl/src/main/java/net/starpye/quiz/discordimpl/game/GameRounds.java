package net.starpye.quiz.discordimpl.game;

import net.starype.quiz.api.game.ClassicalRound;
import net.starype.quiz.api.game.GameRound;
import net.starype.quiz.api.game.IndividualRound;
import net.starype.quiz.api.game.RaceRound;
import net.starype.quiz.api.game.question.Question;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public class GameRounds {

    public static Queue<? extends GameRound> defaultPreset(Question q1, Question q2) {
        return new LinkedList<>(Arrays.asList(
                new ClassicalRound(q1, 1, 3),
                new IndividualRound(q2, 2)));
    }
}
