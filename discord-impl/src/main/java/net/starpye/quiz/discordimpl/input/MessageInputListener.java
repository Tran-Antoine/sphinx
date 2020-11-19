package net.starpye.quiz.discordimpl.input;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.command.*;
import net.starpye.quiz.discordimpl.command.CommandContext.MessageContext;
import net.starpye.quiz.discordimpl.game.GameList;
import net.starpye.quiz.discordimpl.game.LobbyList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MessageInputListener implements Consumer<MessageCreateEvent> {

    private Collection<? extends DiscordCommand> commands;
    private LobbyList lobbyList;
    private GameList gameList;

    public MessageInputListener(LobbyList lobbyList, GameList gameList) {
        this.lobbyList = lobbyList;
        this.gameList = gameList;
        this.commands = initCommands();
    }

    @Override
    public void accept(MessageCreateEvent event) {
        String[] args = event.getMessage().getContent().split(" ");
        Optional<? extends DiscordCommand> optCommand = findByName(args[0]);
        if(optCommand.isEmpty()) {
            return;
        }
        DiscordCommand command = optCommand.get();
        MessageContext messageContext = new MessageContext(
                event.getMessage().getChannel().cast(TextChannel.class).block(),
                event.getMember().get(), // Optional guaranteed to be non-empty because of the filter
                args);

        CommandContext context = new CommandContext(
                messageContext,
                gameList,
                lobbyList);
        command.execute(context);
    }

    private Optional<? extends DiscordCommand> findByName(String name) {
        return commands
                .stream()
                .filter(command -> command.getName().equals(name))
                .findAny();
    }

    private Collection<? extends DiscordCommand> initCommands() {
        return Arrays.asList(
                new CreateLobbyCommand(),
                new JoinLobbyCommand(),
                new StartGameCommand()
        );
    }

    public static Predicate<MessageCreateEvent> createFilter() {
        return (event) ->
                event.getMember().isPresent() && !event.getMember().get().isBot();
    }
}
