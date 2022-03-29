package net.starype.quiz.discordimpl.game;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.starype.quiz.api.database.QuestionQueries;
import net.starype.quiz.api.database.QuestionQuery;
import net.starype.quiz.api.database.QuizQueryable;
import net.starype.quiz.api.question.Question;
import net.starype.quiz.api.round.QuizRound;
import net.starype.quiz.discordimpl.command.RoundAddCommand.PartialRound;
import net.starype.quiz.discordimpl.input.ReactionInputListener;
import net.starype.quiz.discordimpl.input.ReactionInputListener.ReactionContext;
import net.starype.quiz.discordimpl.input.ReactionInputListener.TriggerCondition;
import net.starype.quiz.discordimpl.util.ImageUtils;
import net.starype.quiz.discordimpl.util.MessageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameLobby extends DiscordLogContainer {

    private final Runnable destructLobbyCallback;
    private final String guildId;
    private final String name;
    private final TextChannel channel;
    private final Set<String> playersId;
    private String authorId;

    private final Queue<PartialRound> partialRounds;
    private QuizQueryable queryObject;
    private QuestionQuery query;

    private String lobbyMessageId;

    public GameLobby(TextChannel channel, String name, Runnable destructLobbyCallback) {
        super(channel);
        this.channel = channel;
        this.name = name;
        this.destructLobbyCallback = destructLobbyCallback;
        this.guildId = channel.getGuild().getId();
        this.partialRounds = new LinkedList<>();
        this.playersId = new HashSet<>();
    }

    public void registerAuthor(String playerId, String userName) {
        this.authorId = playerId;
        registerPlayer(playerId, userName);
    }

    public void registerPlayer(String playerId, String userName) {
        if(!playersId.add(playerId)) {
            return;
        }
        String countIndication = countIndication();
        editLobbyMessage(userName + " joined <:pandaheart:803074085101109319> " + countIndication);
    }

    public void unregisterPlayer(String playerID, String userName) {
        if(!playersId.remove(playerID)) {
            return;
        }
        String countIndication = countIndication();
        editLobbyMessage(userName + " left <:pandacry:803074107117142016> " + countIndication);
    }

    private void editLobbyMessage(String newContent) {
        performAction(message -> message.editMessage(newContent));
    }

    private String countIndication() {
        String countIndication = playersId.size() + " player";
        if(playersId.size() > 1) {
            countIndication += "s";
        }
        return "(" + countIndication + ")";
    }

    public boolean containsPlayer(String authorId) {
        return playersId.contains(authorId);
    }

    public boolean start(GameList gameList, Runnable onGameEndedCallback) {

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
        destructLobbyCallback.run();
        gameList.startNewGame(playersId, rounds, channel, authorId, onGameEndedCallback, guildId, true);
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

    public boolean isAuthor(String playerId) {
        return authorId != null && authorId.equals(playerId);
    }

    public boolean isName(String name) {
        return this.name.equals(name);
    }

    public String getName() {
        return name;
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
        Message optMessage = channel
                .sendFile(stream, "image.png")
                .complete();
        return Optional.ofNullable(optMessage);
    }

    private void setUpReaction(Message message, String unicode, Consumer<ReactionContext> action, ReactionInputListener reactionListener) {
        message.addReaction(unicode).queue(null, null);
        TriggerCondition condition = new TriggerCondition(message.getId(), unicode, true);
        reactionListener.addCallBack(condition, action);
    }

    private void performAction(Function<Message, RestAction<Message>> action) {
        channel
                .retrieveMessageById(lobbyMessageId)
                .flatMap(action)
                .queue(null, null);
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
        if(partialRounds.size() < 100) {
            partialRounds.add(round);
        }
    }

    public List<String> retrieveNames() {
        return playersId
                .stream()
                .map(id -> channel.getJDA().retrieveUserById(id).complete())
                .map(User::getName)
                .collect(Collectors.toList());
    }

    public int questionCount() {
        if(queryObject == null) {
            return 0;
        }
        return queryObject.listQuery(query == null ? QuestionQueries.ALL : query).size();
    }

    public void resetRounds() {
        partialRounds.clear();
    }

    public void queueRound(PartialRound round, int repeat) {
        for(int i = 0; i < repeat; i++) {
            queueRound(round);
        }
    }
}
