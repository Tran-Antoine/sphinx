package helloquiz;

import net.starype.quiz.api.player.Player;

public class MockPlayer extends Player<String> {

    public MockPlayer(String name) {
        super(name, name);
    }
}
