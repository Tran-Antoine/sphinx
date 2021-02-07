package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;

import java.util.Collections;
import java.util.List;

public class SimpleGameReport implements GameRoundReport {

    private List<String> rawMessages;
    private List<Standing> orderedStandings;

    public SimpleGameReport(List<Standing> orderedStandings) {
        this(Collections.emptyList(), orderedStandings);
    }

    public SimpleGameReport(List<String> rawMessages, List<Standing> orderedStandings) {
        this.rawMessages = rawMessages;
        this.orderedStandings = orderedStandings;
    }

    @Override
    public List<String> rawMessages() {
        return rawMessages;
    }

    @Override
    public List<Standing> orderedStandings() {
        return orderedStandings;
    }
}
