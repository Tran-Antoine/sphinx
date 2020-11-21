package net.starpye.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.game.GameList;
import net.starpye.quiz.discordimpl.game.LobbyList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class JoinLobbyCommand implements QuizCommand {

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Joins a game from an ID";
    }

    @Override
    public void execute(CommandContext context) {

        Member author = context.getAuthor();
        LobbyList lobbyList = context.getLobbyList();

        Map<Supplier<Boolean>, String> stopConditions = createStopConditions(
                context.getGameList(), lobbyList,
                author.getId(), author.getDisplayName(),
                context.getArgs()
        );
        TextChannel channel = context.getChannel();
        if(StopConditions.shouldStop(stopConditions, channel)) {
            return;
        }
        String lobbyName = context.getArgs()[1];
        lobbyList.findById(lobbyName).get().registerPlayer(author.getId());
        channel.createMessage("Successfully joined lobby with ID " + lobbyName).block();
    }

    private Map<Supplier<Boolean>, String> createStopConditions(
            GameList gameList, LobbyList lobbyList, Snowflake authorId, String nickName, String[] args) {

        Map<Supplier<Boolean>, String> conditions = new HashMap<>();
        conditions.put(
                () -> gameList.isPlaying(authorId),
                nickName + ", you can't join a lobby if you're already playing");

        conditions.put(
                () -> lobbyList.findByPlayer(authorId).isPresent(),
                nickName + ", you are already in a lobby");

        conditions.put(
                () -> args.length < 2,
                nickName + ", you must specify a lobby ID");

        conditions.put(
                () -> lobbyList.findById(args[1]).isEmpty(),
                nickName + ", no lobby with the given ID found");

        return conditions;
    }

}
