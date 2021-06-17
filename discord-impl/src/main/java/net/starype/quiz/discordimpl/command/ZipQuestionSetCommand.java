package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.starype.quiz.api.database.ByteSerializedIO;
import net.starype.quiz.api.database.EntryUpdater;
import net.starype.quiz.api.database.QuestionDatabase;
import net.starype.quiz.api.database.SerializedIO;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;
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
        String authorId = context.getAuthor().getId();
        MessageChannel channel = context.getChannel();
        CommandInteraction interaction = context.getInteraction();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(
                lobbyList,
                authorId
        );

        if(StopConditions.shouldStop(conditions, channel)) {
            return;
        }

        String url = interaction.getOption("link").getAsString();
        Collection<? extends EntryUpdater> updaters = InputUtils.loadEntryUpdaters(url, channel);
        SerializedIO serializedIO = new ByteSerializedIO(new byte[0], new AtomicReference<>());

        GameLobby lobby = lobbyList.findByAuthor(authorId).get();
        QuestionDatabase database = new QuestionDatabase(updaters, serializedIO, false);
        try {
            database.sync();
        } catch (Exception ignored) {
            MessageUtils.createTemporaryMessage("Invalid file", channel);
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
        return "zip-question-set";
    }

    @Override
    public String getDescription() {
        return "Set the set of questions used for the game from a .zip file";
    }

    @Override
    public CommandData getData() {
        return dataTemplate()
                .addOptions(new OptionData(OptionType.STRING, "link", "link to download the file").setRequired(true));
    }
}
