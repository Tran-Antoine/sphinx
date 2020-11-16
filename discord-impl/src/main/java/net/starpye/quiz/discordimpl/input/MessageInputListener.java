package net.starpye.quiz.discordimpl.input;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.starpye.quiz.discordimpl.test.MockQuestion;
import net.starype.quiz.api.game.*;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.server.GameServer;

import java.util.*;
import java.util.function.Consumer;

public class MessageInputListener implements Consumer<MessageCreateEvent> {

    private QuizGame game;
    private Player player;
    private Snowflake channelID;

    @Override
    public void accept(MessageCreateEvent event) {

        String message = event.getMessage().getContent();

        if(message.equals("test-start") && game == null) {
            startGame(event);
            return;
        }

        if(game == null || player == null || channelID == null) {
            return;
        }

        if(!event.getMessage().getChannelId().equals(channelID)) {
            return;
        }
        sendInput(message);
    }

    private void sendInput(String message) {
        if(message.equals("give-up")) {
            game.onInputReceived(player, "");
            return;
        }
        game.onInputReceived(player, message);
    }

    private void startGame(MessageCreateEvent event) {

        this.player = new Player(UUID.randomUUID(), "Antoine Tran");
        this.channelID = event.getMessage().getChannelId();

        GameServer server = new DiscordGameServer(event.getGuild().block(), channelID);
        Queue<GameRound> rounds = new LinkedList<>(Arrays.asList(
                new IndividualRound(1, new MockQuestion()),
                new PollRound(new MockQuestion(), 2)
        ));

        this.game = new SimpleGame(rounds, Collections.singletonList(player), server);
        game.start();
    }
}
