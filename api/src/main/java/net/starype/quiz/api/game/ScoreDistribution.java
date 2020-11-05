package net.starype.quiz.api.game;

import net.starype.quiz.api.user.Player;

import java.util.function.Function;

public interface ScoreDistribution extends Function<Player, Double> { }
