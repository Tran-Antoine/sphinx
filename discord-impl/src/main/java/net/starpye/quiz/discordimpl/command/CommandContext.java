package net.starpye.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;

public class CommandContext {

    private Guild guild;
    private Snowflake authorId;
    private String[] args;

    public CommandContext(Guild guild, Snowflake authorId, String[] args) {
        this.guild = guild;
        this.authorId = authorId;
        this.args = args;
    }

    public Guild getGuild() {
        return guild;
    }

    public Snowflake getAuthorId() {
        return authorId;
    }

    public String[] getArgs() {
        return args;
    }
}
