package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.starype.quiz.discordimpl.game.DiscordQuizGame;
import net.starype.quiz.discordimpl.game.GameList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class KickPlayerCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {

        Member author = context.getAuthor();
        String id = author.getId();
        CommandInteraction interaction = context.getInteraction();
        GameList gameList = context.getGameList();
        User target = interaction.getOption("name").getAsUser();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(gameList, id);
        if(StopConditions.shouldStop(conditions, interaction)) {
            return;
        }

        DiscordQuizGame game = gameList.getFromPlayer(id).get();
        game.removePlayer(target.getId());
        game.checkEndOfRound();

        interaction.getHook().editOriginal("Player successfully excluded from the game")
                .map(Message::getId)
                .queue(game::addLog);
    }


    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "kick a player from a game";
    }

    @Override
    public CommandData getData() {
        return dataTemplate()
                .addOptions(new OptionData(OptionType.USER, "name", "name of the player to kick")
                        .setRequired(true));
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(GameList gameList,
                                                                       String authorId) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> gameList.getFromPlayer(authorId).isEmpty(),
                "You must be in a game to use this");
        conditions.put(
                () -> !gameList.getFromPlayer(authorId).get().isAuthor(authorId),
                "You must be the other of the game to use this");
        return conditions;
    }
}
