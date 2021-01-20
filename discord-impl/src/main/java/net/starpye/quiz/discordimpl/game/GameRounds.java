package net.starpye.quiz.discordimpl.game;

import net.starype.quiz.api.game.ClassicalRound;
import net.starype.quiz.api.game.GameRound;
import net.starype.quiz.api.game.IndividualRound;
import net.starype.quiz.api.game.RaceRound;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class GameRounds {

    public static final Queue<GameRound> DEFAULT_PRESET = new LinkedList<>(Arrays.asList(
            new IndividualRound(Questions.pickRandom(), 1.0),
            new RaceRound(2, Questions.pickRandom(), 2.0),
            new ClassicalRound(Questions.pickRandom(), 3, 3)
    ));
}
