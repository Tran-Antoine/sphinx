package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.discordimpl.game.LobbyList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

// Unused since replaced by a nicer system that only requires users to react on a message to join the game
@Deprecated
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
        String authorName = author.getEffectiveName();

        Map<Supplier<Boolean>, String> stopConditions = createStopConditions(
                context.getGameList(), lobbyList,
                author.getId(), authorName,
                context.getArgs()
        );
        TextChannel channel = context.getChannel();
        if(StopConditions.shouldStop(stopConditions, channel, context.getMessage())) {
            return;
        }
        String lobbyName = context.getArgs()[1];
        lobbyList.findById(lobbyName).get().registerPlayer(author.getId(), authorName);
        channel.sendMessage("Successfully joined lobby with ID " + lobbyName).queue(null, null);
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(
            GameList gameList, LobbyList lobbyList, String authorId, String nickName, String[] args) {

        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
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
