package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.api.database.ByteSerializedIO;
import net.starype.quiz.api.database.QuestionDatabase;
import net.starype.quiz.api.database.SerializedIO;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;
import net.starype.quiz.discordimpl.util.MessageUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;


public class CompiledQuestionSetCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {
        LobbyList lobbyList = context.getLobbyList();
        Member author = context.getAuthor();
        TextChannel channel = context.getChannel();
        Message message = context.getMessage();
        String authorId = author.getId();
        String[] args = context.getArgs();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(
                lobbyList,
                message,
                authorId,
                args);

        if(StopConditions.shouldStop(conditions, channel, message)) {
            return;
        }

        GameLobby lobby = lobbyList.findByAuthor(authorId).get();
        String url = findUrl(message, args);
        byte[] dbData;
        try {
            dbData = new URL(url)
                    .openStream()
                    .readAllBytes();
        } catch (IOException e) {
            channel.sendMessage("Error: Couldn't load .sphinx file").queue();
            return;
        }

        SerializedIO serializedIO = new ByteSerializedIO(dbData, new AtomicReference<>());
        QuestionDatabase database = new QuestionDatabase(Collections.emptyList(), serializedIO, true);
        try {
            database.sync();
        } catch (Exception ignored) {
            MessageUtils.makeTemporary(channel, message);
            MessageUtils.createTemporaryMessage("Invalid file", channel);
            return;
        }
        lobby.setQueryObject(database);

        lobby.trackMessage(message.getId());
        MessageUtils.sendAndTrack(
                "Successfully registered the database",
                channel,
                lobby
        );
    }

    private String findUrl(Message message, String[] args) {
        Collection<Attachment> attachments = message.getAttachments();
        if(attachments.size() == 1) {
            return attachments
                    .iterator()
                    .next()
                    .getUrl();
        }
        return args[1];
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(
            LobbyList lobbyList, Message message, String authorId, String[] args) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> lobbyList.findByAuthor(authorId).isEmpty(),
                "You are not the author of any lobby");

        conditions.put(() -> message.getAttachments().size() != 1 && args.length != 2,
                "You need to attach a single .sphinx file or a link to the file as second argument");

        return conditions;
    }

    @Override
    public String getName() {
        return "compiled-question-set";
    }

    @Override
    public String getDescription() {
        return "Set the set of questions used for the game from a .sphinx file";
    }
}
