package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;

import java.util.List;

public interface GameRoundReport {

    List<String> rawMessages();
    List<Standing> orderedStandings();
}
