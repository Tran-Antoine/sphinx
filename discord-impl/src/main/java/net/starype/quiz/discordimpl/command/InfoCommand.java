package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.discordimpl.game.DiscordQuizGame;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;
import net.starype.quiz.discordimpl.util.MessageUtils;

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
        TextChannel channel = context.getChannel();
        Member member = context.getAuthor();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(lobbyList, gameList, member);
        if(StopConditions.shouldStop(conditions, channel, context.getMessage())) {
            return;
        }

        Optional<GameLobby> lobby = lobbyList.findByPlayer(member.getId());
        if(lobby.isPresent()) {
            displayLobbyInfo(lobby.get(), channel);
        } else {
            displayGameInfo(gameList.getFromPlayer(member).get(), channel);
        }
    }

    private static void displayLobbyInfo(GameLobby lobby, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.CYAN);

        builder.addField("lobby ID", lobby.getName(), false);
        builder.addBlankField(false);
        builder.addField("Players", String.join(", ", lobby.retrieveNames()), false);
        builder.addField("Question count", String.valueOf(lobby.questionCount()), false);

        MessageUtils.sendAndTrack(builder.build(), channel, lobby);
    }

    private static void displayGameInfo(DiscordQuizGame game, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.ORANGE);

        if(game.isOutOfRounds() && game.isWaitingForNextRound()) {
            builder.addField("Game Status", "Waiting to display results", false);
            builder.addBlankField(false);
            builder.addField("Not ready yet", String.join(", ", game.haveNotVoted()), false);
        }
        else if(!game.hasRoundStarted()) {
            builder.addField("Game Status", "Waiting for next round", false);
            builder.addBlankField(false);
            builder.addField("Not ready yet", String.join(", ", game.haveNotVoted()), false);
        } else if(!game.isCurrentRoundFinished()){
            builder.addField("Game Status", "Round in progress", false);
            builder.addBlankField(false);
            builder.addField("Waiting for", String.join(", ", game.waitingFor()), false);
            builder.addField("Have answered", String.join(", ", game.haveAnswered()), false);
        }

        channel.sendMessageEmbeds(builder.build())
                .map(Message::getId)
                .queue(game::addLog);
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(LobbyList lobbyList, GameList gameList, Member author) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> lobbyList.findByPlayer(author.getId()).isEmpty() && gameList.getFromPlayer(author).isEmpty(),
                "You must belong to either a lobby or a game");
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
}
