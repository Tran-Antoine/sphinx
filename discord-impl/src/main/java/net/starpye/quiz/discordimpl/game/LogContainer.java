package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;

public interface LogContainer {

    void trackMessage(Snowflake id);
    void deleteMessages();
}
