package net.starype.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import net.starype.quiz.discordimpl.command.RoundAddCommand.PartialRound;
import net.starype.quiz.discordimpl.input.ReactionInputListener;
import net.starype.quiz.discordimpl.input.ReactionInputListener.ReactionContext;
import net.starype.quiz.discordimpl.input.ReactionInputListener.TriggerCondition;
import net.starype.quiz.discordimpl.util.ImageUtils;
import net.starype.quiz.discordimpl.util.MessageUtils;
import net.starype.quiz.api.database.QuestionQueries;
import net.starype.quiz.api.database.QuestionQuery;
import net.starype.quiz.api.database.QuizQueryable;
import net.starype.quiz.api.game.QuizRound;
import net.starype.quiz.api.question.Question;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GameLobby extends DiscordLogContainer {

    private String name;
    private TextChannel channel;
    private Set<Snowflake> playersId;
    private Snowflake authorId;

    private Queue<PartialRound> partialRounds;
    private QuizQueryable queryObject;
    private QuestionQuery query;

    private Snowflake lobbyMessageId;

    public GameLobby(TextChannel channel, String name) {
        super(channel);
        this.channel = channel;
        this.name = name;
        this.partialRounds = new LinkedList<>();
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

    public boolean start(GameList gameList) {

        if(noQueryObject()) {
            return false;
        }

        if(query == null) {
            this.query = QuestionQueries.ALL;
        }

        if(noRounds()) {
            return false;
        }

        Queue<Question> questions = new LinkedList<>(queryObject.listQuery(query));

        if(notEnoughQuestions(questions)) {
            return false;
        }

        Queue<QuizRound> rounds = partialRounds
                .stream()
                .map(partial -> partial.apply(questions.poll()))
                .collect(Collectors.toCollection(LinkedList::new));

        
        deleteMessages();
        gameList.startNewGame(playersId, rounds, channel, authorId);
        return true;
    }

    private boolean noQueryObject() {
        if(queryObject == null) {
            MessageUtils.createTemporaryMessage(
                    "Please specify which questions you want to play with", channel
            );
            return true;
        }
        return false;
    }

    private boolean notEnoughQuestions(Queue<Question> questions) {
        if(questions.size() < partialRounds.size()) {
            MessageUtils.createTemporaryMessage(
                    "Not enough questions matching the query",
                    channel);
            return true;
        }
        return false;
    }

    private boolean noRounds() {
        if(partialRounds.isEmpty()) {
            MessageUtils.createTemporaryMessage(
                    "Cannot start a game with no rounds. Use ?add-round to queue a round",
                    channel);
            return true;
        }
        return false;
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
        trackMessage(message.getId());
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

    public void resetQuery() {
        this.query = null;
    }

    public void queueRound(PartialRound round) {
        partialRounds.add(round);
    }
}
