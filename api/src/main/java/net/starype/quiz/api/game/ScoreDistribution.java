package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

import java.util.function.Function;

public interface ScoreDistribution extends Function<Player, Double> { }
