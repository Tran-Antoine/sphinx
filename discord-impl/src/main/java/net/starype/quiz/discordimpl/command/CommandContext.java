package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.discordimpl.core.DiscordContext;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.discordimpl.game.LobbyList;

public class CommandContext {

    private final DiscordContext discordContext;
    private MessageContext messageContext;
    private GameList gameList;
    private LobbyList lobbyList;


    public CommandContext(MessageContext messageContext, GameList gameList, LobbyList lobbyList, DiscordContext discordContext) {
        this.messageContext = messageContext;
        this.discordContext = discordContext;
        this.gameList = gameList;
        this.lobbyList = lobbyList;
    }

    public DiscordContext getDiscordContext() {
        return discordContext;
    }

    public TextChannel getChannel() {
        return messageContext.textChannel;
    }

    public Member getAuthor() {
        return messageContext.author;
    }

    public String[] getArgs() {
        return messageContext.args;
    }

    public GameList getGameList() {
        return gameList;
    }

    public LobbyList getLobbyList() {
        return lobbyList;
    }

    public Message getMessage() {
        return messageContext.message;
    }

    public static class MessageContext {

        private TextChannel textChannel;
        private Message message;
        private Member author;
        private String[] args;

        public MessageContext(TextChannel textChannel, Message message, Member author, String[] args) {
            this.textChannel = textChannel;
            this.message = message;
            this.author = author;
            this.args = args;
        }
    }
}
