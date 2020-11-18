package net.starpye.quiz.discordimpl.input;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.command.CommandContext;
import net.starpye.quiz.discordimpl.command.CreateLobbyCommand;
import net.starpye.quiz.discordimpl.command.DiscordCommand;
import net.starpye.quiz.discordimpl.command.StartGameCommand;
import net.starpye.quiz.discordimpl.game.GameList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MessageInputListener implements Consumer<MessageCreateEvent> {

    private Collection<? extends DiscordCommand> commands;
    private GameList gameList;

    public MessageInputListener(GameList gameList) {
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
        CommandContext context = new CommandContext(
                event.getMessage().getChannel().cast(TextChannel.class).block(),
                event.getMember().get().getId(), // Optional guaranteed to be non-empty because of the filter
                args,
                gameList);
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
                new StartGameCommand()
        );
    }

    public static Predicate<MessageCreateEvent> createFilter() {
        return (event) ->
                event.getMember().isPresent() && !event.getMember().get().isBot();
    }
}
