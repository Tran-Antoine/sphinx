package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.api.database.ByteSerializedIO;
import net.starype.quiz.api.database.EntryUpdater;
import net.starype.quiz.api.database.QuestionDatabase;
import net.starype.quiz.api.database.SerializedIO;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;
import net.starype.quiz.discordimpl.util.CounterLimiter;
import net.starype.quiz.discordimpl.util.InputUtils;
import net.starype.quiz.discordimpl.util.MessageUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ZipQuestionSetCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {

        LobbyList lobbyList = context.getLobbyList();
        Message message = context.getMessage();
        String authorId = context.getAuthor().getId();
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
