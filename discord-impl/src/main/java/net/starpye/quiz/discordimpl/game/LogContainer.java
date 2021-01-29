package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;

public interface LogContainer {

    void addLog(Snowflake id);
    void deleteLogs();
}
