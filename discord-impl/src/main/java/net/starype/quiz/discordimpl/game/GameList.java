package net.starype.quiz.discordimpl.game;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.api.round.QuizRound;
import net.starype.quiz.api.server.ServerGate;
import net.starype.quiz.discordimpl.user.DiscordPlayer;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GameList {

    private final Map<DiscordQuizGame, ScheduledFuture<?>> ongoingGames;

    public GameList() {
        this.ongoingGames = new HashMap<>();
    }

    public void startNewGame(Collection<? extends String> playersId, Queue<? extends QuizRound> rounds, TextChannel channel, String authorId,
                             Runnable onGameEndedCallback, String guildId, boolean fixedPlayerList) {

        Consumer<DiscordQuizGame> naturalEndAction = game -> {
            this.stopGame(game);
            onGameEndedCallback.run();
        };


        Collection<DiscordPlayer> gamePlayers = asGamePlayers(playersId, channel);
        DiscordGameServer server = new DiscordGameServer(channel, naturalEndAction);
        ServerGate<DiscordQuizGame> gate = server.createGate();
        Guild guild = fromId(channel, guildId);
        DiscordQuizGame game = new DiscordQuizGame(rounds, gamePlayers, gate, authorId, server, guild, fixedPlayerList);

        Runnable forcedEndAction = () -> {
            stopGame(game, true, channel);
            onGameEndedCallback.run();
        };

        ScheduledExecutorService autoCancel = Executors.newScheduledThreadPool(1);
        autoCancel.schedule(forcedEndAction, 120, TimeUnit.MINUTES);

        ScheduledExecutorService autoUpdate = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = autoUpdate.scheduleAtFixedRate(game::update, 0, 250, TimeUnit.MILLISECONDS);
        game.start();
        ongoingGames.put(game, future);
    }

    private Guild fromId(MessageChannel channel, String guildId) {
        return channel.getJDA().getGuildById(guildId);
    }

    private Collection<DiscordPlayer> asGamePlayers(Collection<? extends String> playersId, TextChannel channel) {
        Guild guild = channel.getGuild();
        return playersId
                .stream()
                .map(id -> guild.retrieveMemberById(id).complete())
                .map(this::asPlayer)
                .collect(Collectors.toList());
    }

    public boolean isPlaying(Member player) {
        return ongoingGames
                .keySet()
                .stream()
                .anyMatch((game) -> game.containsPlayerId(player.getId()));
    }

    public boolean canPlay(Member player) {
        return findGameFor(player, false).isPresent();
    }

    public Optional<DiscordQuizGame> getFromPlayer(Member player) {
        return ongoingGames
                .keySet()
                .stream()
                .filter(game -> game.containsPlayerId(player.getId()))
                .findAny();
    }

    public Optional<DiscordQuizGame> findGameFor(Member player, boolean addIfFound) {
        return getFromPlayer(player)
                .or(() -> findNewGameFor(player, player.getGuild().getId(), addIfFound));
    }

    private Optional<DiscordQuizGame> findNewGameFor(Member player, String guildId, boolean addIfFound) {
        Optional<DiscordQuizGame> potentialGame = ongoingGames
                .keySet()
                .stream()
                .filter(DiscordQuizGame::supportsNonFixedPlayerList)
                .filter(game -> game.assignedGuild().getId().equals(guildId))
                .findAny();
        if(addIfFound) {
            potentialGame.ifPresent(game -> game.insertNewPlayer(asPlayer(player)));
        }
        return potentialGame;
    }

    public void stopGame(DiscordQuizGame game) {
        stopGame(game, false, null);
    }

    public void stopGame(DiscordQuizGame game, boolean forced, TextChannel channel) {
        ScheduledFuture<?> future = ongoingGames.remove(game);
        if(future == null) {
            return;
        }
        future.cancel(true);

        if(forced) {
            game.deleteLogs();
            channel.sendMessage("Game lasted too long, I had to stop it <:pandasad:805105368505384970>").queue(null, null);
        }
    }

    private DiscordPlayer asPlayer(Member member) {
        String userName = member.getUser().getName();
        String nickName = member.getEffectiveName();
        String id = member.getId();
        return new DiscordPlayer(id, userName, nickName);
    }
}
