package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import net.starpye.quiz.discordimpl.input.ReactionInputListener;
import net.starpye.quiz.discordimpl.input.ReactionInputListener.TriggerCondition;
import net.starpye.quiz.discordimpl.util.ImageUtils;
import net.starype.quiz.api.game.GameRound;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class GameLobby {

    private String name;
    private TextChannel channel;
    private Set<Snowflake> playersId;
    private Snowflake authorId;
    private Queue<GameRound> rounds;

    private Snowflake lobbyMessageId;

    public GameLobby(TextChannel channel, String name) {
        this.channel = channel;
        this.name = name;
        this.playersId = new HashSet<>();
        this.rounds = GameRounds.DEFAULT_PRESET;
    }

    public void registerAuthor(Snowflake playerId, String userName) {
        this.authorId = playerId;
        registerPlayer(playerId, userName);
    }

    public void registerPlayer(Snowflake playerId, String userName) {
        if(!playersId.add(playerId)) {
            return;
        }
        String countIndication = playersId.size() + " player";
        if(playersId.size() > 1) {
            countIndication += "s";
        }
        editLobbyMessage(userName + " joined! (" + countIndication + ")");
    }

    private void editLobbyMessage(String newContent) {
        channel
                .getMessageById(lobbyMessageId)
                .block()
                .edit(spec -> spec.setContent(newContent))
                .block();
    }

    public void unregisterPlayer(Snowflake playerID, String userName) {
        if(!playersId.remove(playerID)) {
            return;
        }
        editLobbyMessage(userName + " left :(");
    }

    public void queueRound(GameRound round) {
        rounds.add(round);
    }

    public void unqueueRound(GameRound round) {
        rounds.remove(round);
    }

    public boolean containsPlayer(Snowflake authorId) {
        return playersId.contains(authorId);
    }

    public void start(GameList gameList) {
        gameList.startNewGame(playersId, rounds, channel, authorId);
    }

    public boolean isAuthor(Snowflake playerId) {
        return authorId != null && authorId.equals(playerId);
    }

    public boolean isName(String name) {
        return this.name.equals(name);
    }

    public void sendJoinImage(ReactionInputListener reactionListener, TextChannel channel) {

        try {
            BufferedImage image = ImageIO.read(new File("discord-impl/src/main/resources/lobby.png"));
            InputStream stream = ImageUtils.toInputStream(image);
            Message message = channel.createMessage(spec -> spec.addFile("image.png", stream)).block();
            message.addReaction(ReactionEmoji.unicode("\u25B6")).block();
            message.addReaction(ReactionEmoji.unicode("\u274C")).block();
            this.lobbyMessageId = message.getId();
            TriggerCondition joinOption = new TriggerCondition(lobbyMessageId, "\u25B6", true);
            TriggerCondition leaveOption = new TriggerCondition(lobbyMessageId, "\u274C", true);
            reactionListener.addCallBack(joinOption, cont -> registerPlayer(cont.getUserId(), cont.getUserName()));
            reactionListener.addCallBack(leaveOption, cont -> unregisterPlayer(cont.getUserId(), cont.getUserName()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
