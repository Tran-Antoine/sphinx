package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.discordimpl.game.LobbyList;

public class CommandContext {

    private final CommandInteraction interaction;
    private final GameList gameList;
    private final LobbyList lobbyList;

    public CommandContext(CommandInteraction interaction, GameList gameList, LobbyList lobbyList) {
        this.interaction = interaction;
        this.gameList = gameList;
        this.lobbyList = lobbyList;
    }

    public MessageChannel getChannel() {
        return interaction.getChannel();
    }

    public Member getAuthor() {
        return interaction.getMember();
    }

    public GameList getGameList() {
        return gameList;
    }

    public LobbyList getLobbyList() {
        return lobbyList;
    }

    public CommandInteraction getInteraction() {
        return interaction;
    }
}
