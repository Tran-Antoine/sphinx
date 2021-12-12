package net.starype.quiz.discordimpl.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.discordimpl.game.LobbyList;
import net.starype.quiz.discordimpl.input.MessageInputListener;
import net.starype.quiz.discordimpl.input.ReactionInputListener;

public class BotMain {

    public static void main(String[] args) throws Exception {
        
        JDABuilder builder = JDABuilder.createDefault(System.getenv("BOT_TOKEN"));
        JDA jda = builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.playing("supervising the quiz world championship"))
                .build();

        ReactionInputListener reactionListener = new ReactionInputListener();
        jda.addEventListener(new MessageInputListener(new LobbyList(reactionListener), new GameList()));
        jda.addEventListener(reactionListener);
    }
}
