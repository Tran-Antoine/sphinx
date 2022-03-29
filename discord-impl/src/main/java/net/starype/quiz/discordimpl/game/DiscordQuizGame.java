package net.starype.quiz.discordimpl.game;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.starype.quiz.api.game.SimpleGame;
import net.starype.quiz.api.round.QuizRound;
import net.starype.quiz.api.server.ServerGate;
import net.starype.quiz.discordimpl.user.DiscordPlayer;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DiscordQuizGame extends SimpleGame<DiscordQuizGame> {

    private final Set<String> votesForNext;
    private final String authorId;
    private final LogContainer container;
    private final TextChannel channel;
    private final Collection<DiscordPlayer> players;
    private final Guild guild;
    private final boolean fixedPlayerList;

    public DiscordQuizGame(
            Queue<? extends QuizRound> rounds,
            Collection<DiscordPlayer> players,
            ServerGate<DiscordQuizGame> gate,
            String authorId,
            LogContainer container, Guild guild, TextChannel channel, boolean fixedPlayerList) {

        super(rounds, players);
        this.players = players;
        this.authorId = authorId;
        this.container = container;
        this.channel = channel;
        this.votesForNext = new HashSet<>();
        this.guild = guild;
        this.fixedPlayerList = fixedPlayerList;
        this.setGate(gate.withGame(this));
    }

    @Override
    public boolean nextRound() {
        deleteLogs();
        return super.nextRound();
    }

    public boolean addVote(String playerId, Runnable ifReady) {
        if(!isWaitingForNextRound()) {
            return false;
        }
        votesForNext.add(playerId);
        if(votesForNext.size() < players.size()) {
            return false;
        }
        votesForNext.clear();
        if(ifReady != null) {
            ifReady.run();
        }
        this.nextRound();
        return true;
    }

    @Override
    public void removePlayer(Object playerId) {
        addVote((String) playerId, null);
        super.removePlayer(playerId);
    }

    public boolean isAuthor(String playerId) {
        return playerId.equals(authorId);
    }

    public void deleteLogs() {
        container.deleteMessages();
    }

    public void addLog(String id) {
        container.trackMessage(id);
    }

    public List<String> waitingFor() {
        return retrievePlayers(p -> getCurrentRound().getPlayerEligibility().isEligible(p));
    }

    public List<String> haveAnswered() {
        return retrievePlayers(p -> !getCurrentRound().getPlayerEligibility().isEligible(p));
    }

    public List<String> allPlayerNames() {
        return retrievePlayers(p -> true);
    }

    public List<String> haveNotVoted() {
        return retrievePlayers(p -> !votesForNext.contains(p.getId()));
    }

    public boolean hasRoundStarted() {
        return getCurrentRound().hasStarted();
    }

    private List<String> retrievePlayers(Predicate<DiscordPlayer> filter) {
        return players
                .stream()
                .filter(filter)
                .map(p -> guild.getJDA().retrieveUserById(p.getId()).complete())
                .map(User::getName)
                .collect(Collectors.toList());
    }

    public void checkEndOfRound() {
        getCurrentRound().checkEndOfRound();
    }

    public void insertNewPlayer(DiscordPlayer player) {
        if(fixedPlayerList) {
            throw new IllegalStateException("Cannot insert player in a fixed player list setting");
        }
        players.add(player);
    }

    public boolean supportsNonFixedPlayerList() {
        return !fixedPlayerList;
    }

    public String assignedGuildId() {
        return guild.getId();
    }

    public String assignedChannelId() {
        return channel.getId();
    }
}
