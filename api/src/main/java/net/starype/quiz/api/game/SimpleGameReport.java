package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SimpleGameReport implements GameRoundReport {

    private final List<String> rawMessages;
    private final List<Standing> orderedStandings;
    private final String displayableCorrectAnswer;
    private final Collection<? extends PlayerReport> playerReports;

    public SimpleGameReport(List<Standing> orderedStandings, String displayableCorrectAnswer,
                            Collection<? extends PlayerReport> playerReports) {
        this(Collections.emptyList(), orderedStandings, displayableCorrectAnswer, playerReports);
    }

    public SimpleGameReport(List<String> rawMessages, List<Standing> orderedStandings, String , String displayableCorrectAnswer,
                            Collection<? extends PlayerReport> playerReports) {
        this.rawMessages = rawMessages;
        this.orderedStandings = orderedStandings;
        this.displayableCorrectAnswer = displayableCorrectAnswer;
        this.playerReports = playerReports;
    }

    @Override
    public List<String> rawMessages() {
        return rawMessages;
    }

    @Override
    public List<Standing> orderedStandings() {
        return orderedStandings;
    }

    @Override
    public Collection<? extends PlayerReport> playerReports() {
        return playerReports;
    }

    @Override
    public String displayableCorrectAnswer() {
        return displayableCorrectAnswer;
    }
}
