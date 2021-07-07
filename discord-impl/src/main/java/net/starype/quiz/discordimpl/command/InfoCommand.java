package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.starype.quiz.discordimpl.game.DiscordQuizGame;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class InfoCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {

        GameList gameList = context.getGameList();
        LobbyList lobbyList = context.getLobbyList();
        CommandInteraction interaction = context.getInteraction();
        String id = context.getAuthor().getId();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(lobbyList, gameList, id);
        if(StopConditions.shouldStop(conditions, interaction)) {
            return;
        }

        Optional<GameLobby> lobby = lobbyList.findByPlayer(id);
        if(lobby.isPresent()) {
            displayLobbyInfo(lobby.get(), interaction);
        } else {
            //displayGameInfo();
        }
    }

    private void displayLobbyInfo(GameLobby lobby, CommandInteraction interaction) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.CYAN);

        builder.addField("lobby ID", lobby.getName(), false);
        builder.addField("Players", String.join(",", lobby.retrieveNames()), false);
        builder.addField("Questions count", String.valueOf(lobby.questionsCount()), false);

        interaction.getHook().editOriginalEmbeds(builder.build()).queue();
    }

    private void displayGameInfo(DiscordQuizGame game) {

    }

    private static Map<Supplier<Boolean>, String> createStopConditions(LobbyList lobbyList, GameList gameList, String authorId) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> lobbyList.findByPlayer(authorId).isEmpty() && gameList.getFromPlayer(authorId).isEmpty(),
                "You must belong to a lobby / game");
        return conditions;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Get relevant information on your lobby/game";
    }

    @Override
    public CommandData getData() {
        return dataTemplate();
    }
}
