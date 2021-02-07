package net.starpye.quiz.discordimpl.game;

import net.starype.quiz.api.game.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class GameRounds {

    public static final Queue<GameRound> DEFAULT_PRESET = new LinkedList<>(Arrays.asList(
            new IndividualRoundFactory().create(Questions.pickRandom(), 1.0),
            new RaceRoundFactory().create( Questions.pickRandom(), 2, 2.0),
            new ClassicalRoundFactory().create(Questions.pickRandom(), 3, 3)
    ));
}
