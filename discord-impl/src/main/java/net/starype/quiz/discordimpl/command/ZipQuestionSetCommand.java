package net.starype.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;
import net.starype.quiz.discordimpl.util.InputUtils;
import net.starype.quiz.discordimpl.util.MessageUtils;
import net.starype.quiz.api.database.ByteSerializedIO;
import net.starype.quiz.api.database.EntryUpdater;
import net.starype.quiz.api.database.QuestionDatabase;
import net.starype.quiz.api.database.SerializedIO;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ZipQuestionSetCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {

        LobbyList lobbyList = context.getLobbyList();
        Message message = context.getMessage();
        Snowflake authorId = context.getAuthor().getId();
        TextChannel channel = context.getChannel();
        String[] args = context.getArgs();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(
                lobbyList,
                message,
                authorId,
                args);

        if(StopConditions.shouldStop(conditions, channel, message)) {
            return;
        }

        String url = findUrl(message, args);
        Collection<? extends EntryUpdater> updaters = InputUtils.loadEntryUpdaters(url, channel);
        SerializedIO serializedIO = new ByteSerializedIO(new byte[0], new AtomicReference<>());

        GameLobby lobby = lobbyList.findByAuthor(authorId).get();
        QuestionDatabase database = new QuestionDatabase(updaters, serializedIO, false);
        database.sync();

        lobby.setQueryObject(database);

        lobby.trackMessage(message.getId());
        MessageUtils.sendAndTrack(
                "Successfully registered the database",
                channel,
                lobby
        );
    }

    private String findUrl(Message message, String[] args) {
        Set<Attachment> attachments = message.getAttachments();
        if(attachments.size() == 1) {
            return attachments
                    .iterator()
                    .next()
                    .getUrl();
        }
        return args[1];
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(
            LobbyList lobbyList, Message message, Snowflake authorId, String[] args) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> lobbyList.findByAuthor(authorId).isEmpty(),
                "You are not the author of any lobby");

        conditions.put(() -> message.getAttachments().size() != 1 && args.length != 2,
                "You need to attach a single .zip file or a link to the file as second argument");

        return conditions;
    }

    @Override
    public String getName() {
        return "zip-question-set";
    }

    @Override
    public String getDescription() {
        return "Set the set of questions used for the game from a .zip file";
    }
}
