package net.starype.quiz.api.server;

import net.starype.quiz.api.game.GameRoundReport;

public interface RoundReportProcessor {

    void process(GameRoundReport report);
}
