package net.starype.quiz.discordimpl.input;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.starype.quiz.discordimpl.command.*;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.discordimpl.game.LobbyList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageInputListener extends ListenerAdapter {

    private final Collection<QuizCommand> commands;
    private final LobbyList lobbyList;
    private final GameList gameList;

    public MessageInputListener(LobbyList lobbyList, GameList gameList) {
        this.lobbyList = lobbyList;
        this.gameList = gameList;
        this.commands = initCommands();
        this.commands.add(new HelpCommand(this.commands));
    }


    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {

        if(event.getGuild() == null || event.getChannelType() != ChannelType.TEXT || event.getUser().isBot()) {
            return;
        }

        String commandName = event.getName();
        Optional<? extends QuizCommand> optCommand = findByName(commandName);

        event.deferReply().queue();
        optCommand.ifPresent(command -> processCommand(
                command,
                event));
    }

    private void processCommand(QuizCommand command, CommandInteraction interaction) {
        CommandContext context = new CommandContext(interaction, gameList, lobbyList);
        command.execute(context);
    }

    private Optional<? extends QuizCommand> findByName(String name) {
        return commands
                .stream()
                .filter(command -> command.getName().equals(name))
                .findAny();
    }

    private Collection<QuizCommand> initCommands() {
        return new ArrayList<>(Arrays.asList(
                new CreateLobbyCommand(),
                new StartGameCommand(),
                new SubmitCommand(),
                new LeaveCommand(),
                new NextRoundCommand(),
                new ForceNextRoundCommand(),
                new ProcedureDisplayCommand(),
                new GenerateDatabaseCommand(),
                new CompiledQuestionSetCommand(),
                new ZipQuestionSetCommand(),
                new QueryAddCommand(),
                new ClearQueryCommand(),
                new RoundAddCommand(),
                new InfoCommand()
        ));
    }

    public Collection<? extends CommandData> getCommandsData() {
        return commands
                .stream()
                .map(DisplayableCommand::getData)
                .collect(Collectors.toList());
    }
}
