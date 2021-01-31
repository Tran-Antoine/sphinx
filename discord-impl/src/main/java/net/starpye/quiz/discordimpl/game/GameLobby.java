package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import net.starpye.quiz.discordimpl.input.ReactionInputListener;
import net.starpye.quiz.discordimpl.input.ReactionInputListener.ReactionContext;
import net.starpye.quiz.discordimpl.input.ReactionInputListener.TriggerCondition;
import net.starpye.quiz.discordimpl.util.ImageUtils;
import net.starype.quiz.api.database.QuestionQueries;
import net.starype.quiz.api.database.QuestionQuery;
import net.starype.quiz.api.database.QuizQueryable;
import net.starype.quiz.api.game.GameRound;
import net.starype.quiz.api.game.question.Question;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class GameLobby {

    private String name;
    private TextChannel channel;
    private Set<Snowflake> playersId;
    private Snowflake authorId;

    private QuizQueryable queryObject;
    private QuestionQuery query;

    private Snowflake lobbyMessageId;

    public GameLobby(TextChannel channel, String name) {
        this.channel = channel;
        this.name = name;
        this.playersId = new HashSet<>();
    }

    public void registerAuthor(Snowflake playerId, String userName) {
        this.authorId = playerId;
        registerPlayer(playerId, userName);
    }

    public void registerPlayer(Snowflake playerId, String userName) {
        if(!playersId.add(playerId)) {
            return;
        }
        String countIndication = countIndication();
        editLobbyMessage(userName + " joined <:pandaheart:803074085101109319> " + countIndication);
    }

    public void unregisterPlayer(Snowflake playerID, String userName) {
        if(!playersId.remove(playerID)) {
            return;
        }
        String countIndication = countIndication();
        editLobbyMessage(userName + " left <:pandacry:803074107117142016> " + countIndication);
    }

    private void editLobbyMessage(String newContent) {
        performAction(message -> message.edit(spec -> spec.setContent(newContent)).subscribe());
    }

    private String countIndication() {
        String countIndication = playersId.size() + " player";
        if(playersId.size() > 1) {
            countIndication += "s";
        }
        return "(" + countIndication + ")";
    }

    public boolean containsPlayer(Snowflake authorId) {
        return playersId.contains(authorId);
    }

    public void start(GameList gameList) {
        Optional<Question> optQuestion = queryObject.pickQuery(query);
        if(optQuestion.isEmpty()) {
            channel.createMessage("No question found").subscribe();
            return;
        }
        gameList.startNewGame(playersId, GameRounds.defaultPreset(optQuestion.get()), channel, authorId);
        performAction(message -> message.delete().subscribe());
    }

    public boolean isAuthor(Snowflake playerId) {
        return authorId != null && authorId.equals(playerId);
    }

    public boolean isName(String name) {
        return this.name.equals(name);
    }

    public void sendJoinImage(ReactionInputListener reactionListener) {

        Optional<Message> optMessage = sendImage();

        if(optMessage.isEmpty()) {
            System.err.println("Error: Image could not be sent");
            return;
        }

        Message message = optMessage.get();
        this.lobbyMessageId = message.getId();

        setUpReaction(
                message, "\u25B6",
                cont -> registerPlayer(cont.getUserId(), cont.getUserName()),
                reactionListener
        );

        setUpReaction(
                message, "\u274C",
                cont -> unregisterPlayer(cont.getUserId(), cont.getUserName()),
                reactionListener
        );
    }

    private Optional<Message> sendImage() {

        BufferedImage image;
        try {
            image = ImageIO.read(new File("discord-impl/src/main/resources/lobby.png"));
        } catch (IOException e) {
            return Optional.empty();
        }

        InputStream stream = ImageUtils.toInputStream(image);
        Optional<Message> optMessage = channel
                .createMessage(spec -> spec.addFile("image.png", stream))
                .blockOptional();
        return optMessage;
    }

    private void setUpReaction(Message message, String unicode, Consumer<ReactionContext> action, ReactionInputListener reactionListener) {
        message.addReaction(ReactionEmoji.unicode(unicode)).block();
        TriggerCondition condition = new TriggerCondition(message.getId(), unicode, true);
        reactionListener.addCallBack(condition, action);
    }

    private void performAction(Consumer<Message> action) {
        channel
                .getMessageById(lobbyMessageId)
                .blockOptional()
                .ifPresent(action);
    }

    public void setQueryObject(QuizQueryable queryObject) {
        this.queryObject = queryObject;
    }

    public void andQuery(QuestionQuery query) {
       if(this.query == null) {
            this.query = query;
            return;
       }
       this.query = this.query.and(query);
    }

    public void orQuery(QuestionQuery query) {
        if(this.query == null) {
            this.query = query;
            return;
        }
        this.query = this.query.or(query);
    }
}
