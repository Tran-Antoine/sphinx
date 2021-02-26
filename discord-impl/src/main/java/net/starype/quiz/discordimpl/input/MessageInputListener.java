package net.starype.quiz.discordimpl.input;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.starype.quiz.discordimpl.command.*;
import net.starype.quiz.discordimpl.command.CommandContext.MessageContext;
import net.starype.quiz.discordimpl.util.DiscordContext;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.discordimpl.game.LobbyList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class MessageInputListener extends ListenerAdapter {

    public static final String PREFIX = "?";
    private final DiscordContext discordContext;

    private Collection<QuizCommand> commands;
    private LobbyList lobbyList;
    private GameList gameList;

    public MessageInputListener(LobbyList lobbyList, GameList gameList, DiscordContext discordContext) {
        this.lobbyList = lobbyList;
        this.discordContext = discordContext;
        this.gameList = gameList;
        this.commands = initCommands();
        this.commands.add(new HelpCommand(this.commands));
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if(event.getAuthor().isBot()) {
            return;
        }

        Message message = event.getMessage();
        String content = message.getContentDisplay();
        if(!content.startsWith(PREFIX)) {
            return;
        }

        String[] args = content.split(" ");

        Optional<? extends QuizCommand> optCommand = findByName(args[0].replace(PREFIX, ""));
        optCommand.ifPresent(command -> processCommand(
                command,
                message.getTextChannel(),
                message,
                event.getMember(), // Optional guaranteed to be present because of the filter
                args));
    }

    private void processCommand(QuizCommand command, TextChannel channel, Message message, Member member, String... args) {
        MessageContext messageContext = new MessageContext(channel, message, member, args);
        CommandContext context = new CommandContext(messageContext, gameList, lobbyList, discordContext);
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
                new RoundAddCommand()
        ));
    }
}
