package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.starype.quiz.api.database.ByteSerializedIO;
import net.starype.quiz.api.database.QuestionDatabase;
import net.starype.quiz.api.database.SerializedIO;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;
import net.starype.quiz.discordimpl.util.MessageUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;


public class CompiledQuestionSetCommand implements QuizCommand {

    private static final int MAX_BYTES_READ = 1 << 15;

    @Override
    public void execute(CommandContext context) {
        LobbyList lobbyList = context.getLobbyList();
        Member author = context.getAuthor();
        MessageChannel channel = context.getChannel();
        String authorId = author.getId();
        CommandInteraction interaction = context.getInteraction();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(
                lobbyList,
                authorId
        );

        if(StopConditions.shouldStop(conditions, channel)) {
            return;
        }

        GameLobby lobby = lobbyList.findByAuthor(authorId).get();
        String url = interaction.getOption("link").getAsString();

        byte[] dbData;
        try {
            dbData = new URL(url)
                    .openStream()
                    .readNBytes(MAX_BYTES_READ);
        } catch (IOException e) {
            channel.sendMessage("Error: Couldn't load .sphinx file").queue();
            return;
        }

        SerializedIO serializedIO = new ByteSerializedIO(dbData, new AtomicReference<>());
        QuestionDatabase database = new QuestionDatabase(Collections.emptyList(), serializedIO, true);
        try {
            database.sync();
        } catch (Exception ignored) {
            MessageUtils.createTemporaryMessage("Sadly, the provided file is too long or invalid <:pandaisu:805381728805453874>", channel);
            return;
        }
        lobby.setQueryObject(database);

        MessageUtils.sendAndTrack(
                "Successfully registered the database",
                channel,
                lobby
        );
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(
            LobbyList lobbyList, String authorId) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> lobbyList.findByAuthor(authorId).isEmpty(),
                "You are not the author of any lobby");

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

    @Override
    public CommandData getData() {
        return dataTemplate()
                .addOptions(new OptionData(OptionType.STRING, "link", "link to download the file").setRequired(true));
    }
}
