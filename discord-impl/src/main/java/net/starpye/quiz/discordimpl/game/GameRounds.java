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

    public static Queue<? extends GameRound> defaultPreset(Question question) {
        return new LinkedList<>(
                Collections.singletonList(new ClassicalRound(question, 3, 3)));
    }
}
