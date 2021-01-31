package net.starpye.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.game.GameList;
import net.starpye.quiz.discordimpl.game.GameLobby;
import net.starpye.quiz.discordimpl.game.LobbyList;
import net.starype.quiz.api.database.ByteSerializedIO;
import net.starype.quiz.api.database.QuestionDatabase;
import net.starype.quiz.api.database.QuizQueryable;
import net.starype.quiz.api.database.SerializedIO;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class QuestionSetCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {
        LobbyList lobbyList = context.getLobbyList();
        Member author = context.getAuthor();
        TextChannel channel = context.getChannel();
        Message message = context.getMessage();
        Snowflake authorId = author.getId();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(
                lobbyList,
                message,
                authorId,
                context.getArgs());

        if(StopConditions.shouldStop(conditions, channel)) {
            return;
        }

        GameLobby lobby = lobbyList.findByAuthor(authorId).get();
        byte[] dbData;
        try {
            dbData = new URL(message
                    .getAttachments()
                    .iterator()
                    .next()
                    .getUrl())
                    .openStream()
                    .readAllBytes();
        } catch (IOException e) {
            channel.createMessage("Error: Couldn't load .sphinx file").subscribe();
            return;
        }

        SerializedIO serializedIO = new ByteSerializedIO(dbData, new AtomicReference<>());
        QuestionDatabase database = new QuestionDatabase(Collections.emptyList(), serializedIO, true);
        database.sync();
        lobby.setQueryObject(database);
        channel.createMessage("Successfully registered the database").subscribe();
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(
            LobbyList lobbyList, Message message, Snowflake authorId, String[] args) {
        Map<Supplier<Boolean>, String> conditions = new HashMap<>();
        conditions.put(
                () -> lobbyList.findByAuthor(authorId).isEmpty(),
                "You are not the author of any lobby");

        conditions.put(() -> message.getAttachments().size() != 1 && args.length != 2,
                "You need to attach a single .sphinx file or a link to the file");
        return conditions;
    }

    @Override
    public String getName() {
        return "question-set";
    }

    @Override
    public String getDescription() {
        return "Set the set of questions used for the game";
    }
}
