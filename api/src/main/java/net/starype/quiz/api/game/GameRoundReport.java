package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.game.player.IDHolder;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface GameRoundReport {

    List<String> rawMessages();
    List<Standing> orderedStandings();
    Collection<? extends PlayerReport> playerReports();
    String displayableCorrectAnswer();

}
