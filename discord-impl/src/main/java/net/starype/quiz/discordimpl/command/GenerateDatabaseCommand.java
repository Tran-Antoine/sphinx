
package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.starype.quiz.api.database.*;
import net.starype.quiz.discordimpl.util.InputUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class GenerateDatabaseCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {

        CommandInteraction interaction = context.getInteraction();

        String inputName = interaction.getOption("link").getAsString();
        String outputName = Optional.ofNullable(interaction.getOption("name"))
                .map(OptionMapping::getAsString)
                .orElse("database");

        Optional<InputStream> database = generateFile(inputName, interaction);
        if(database.isEmpty()) {
            return;
        }
        interaction.getHook()
                .sendMessage("Here is your output <:pandasoda:839152193462337576>")
                .addFile(database.get(), outputName + ".sphinx")
                .queue(null, null);
    }

    private static Optional<InputStream> generateFile(String urlName, CommandInteraction interaction) {
        Collection<? extends EntryUpdater> updaters = InputUtils.loadEntryUpdaters(urlName, interaction);
        AtomicReference<ByteBuffer> output = new AtomicReference<>();
        SerializedIO serializedIO = new ByteSerializedIO(new byte[0], output);
        TrackedDatabase db = new QuestionDatabase(updaters, serializedIO, false);
        try {
            db.sync();
        } catch(RuntimeException ignored) {
            interaction.getHook().sendMessage("Error: couldn't parse the given zip archive").queue(null, null);
            return Optional.empty();
        }
        return Optional.of(new ByteArrayInputStream(output.get().array()));
    }

    @Override
    public String getName() {
        return "gen-db";
    }

    @Override
    public String getDescription() {
        return "Generate a DB file from a zip of questions put into TOML files";
    }

    @Override
    public CommandData getData() {
        return dataTemplate().addOptions(
                new OptionData(OptionType.STRING, "link", "link to download the file").setRequired(true),
                new OptionData(OptionType.STRING, "name", "name of the output file").setRequired(false));
    }
}
