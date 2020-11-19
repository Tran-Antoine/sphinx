package net.starpye.quiz.discordimpl.core;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.starpye.quiz.discordimpl.game.GameList;
import net.starpye.quiz.discordimpl.game.LobbyList;
import net.starpye.quiz.discordimpl.input.MessageInputListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BotMain {

    public static void main(String[] args) throws IOException {
        GatewayDiscordClient client = DiscordClientBuilder.create(readToken())
                .build()
                .login()
                .block();
        client.getEventDispatcher()
                .on(MessageCreateEvent.class)
                .filter(MessageInputListener.createFilter())
                .subscribe(new MessageInputListener(new LobbyList(), new GameList()));
        client.onDisconnect().block();
    }

    private static String readToken() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("discord-impl/src/main/resources/token.txt"));
        return reader.readLine();
    }
}
