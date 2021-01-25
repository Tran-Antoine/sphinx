package net.starpye.quiz.discordimpl.core;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.spec.MessageCreateSpec;
import net.starpye.quiz.discordimpl.game.GameList;
import net.starpye.quiz.discordimpl.game.LobbyList;
import net.starpye.quiz.discordimpl.input.MessageInputListener;
import net.starpye.quiz.discordimpl.input.ReactionInputListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BotMain {

    public static void main(String[] args) throws IOException {
        GatewayDiscordClient client = DiscordClientBuilder.create(readToken())
                .build()
                .login()
                .block();
        ReactionInputListener reactionListener = new ReactionInputListener();
        EventDispatcher dispatcher = client.getEventDispatcher();

        dispatcher
                .on(ReactionAddEvent.class)
                .filter(ReactionInputListener.createFilter())
                .retry()
                .subscribe(reactionListener);
        dispatcher
                .on(MessageCreateEvent.class)
                .filter(MessageInputListener.createFilter())
                .retry()
                .subscribe(new MessageInputListener(new LobbyList(reactionListener), new GameList()));
        client.onDisconnect().block();
    }

    private static String readToken() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("discord-impl/src/main/resources/token.txt"));
        return reader.readLine();
    }
}
