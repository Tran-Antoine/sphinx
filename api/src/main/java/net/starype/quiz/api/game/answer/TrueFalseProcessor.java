package net.starype.quiz.api.game.answer;

import java.util.Arrays;
import java.util.Collection;

public class TrueFalseProcessor implements AnswerProcessor {

    private static final Collection<String> TRUE_VALUES = Arrays.asList(
            "true", "t",
            "vrai", "v",
            "vraie", "oui"
    );

    private static final Collection<String> FALSE_VALUES = Arrays.asList(
            "false", "f",
            "faux",
            "fausse", "non"
    );

    public static final Answer TRUE = Answer.fromString("True");
    public static final Answer FALSE = Answer.fromString("False");

    @Override
    public Answer process(Answer answer) {
        String text = answer.getAnswerText().toLowerCase();
        if(TRUE_VALUES.contains(text)) {
            return TRUE;
        }
        if(FALSE_VALUES.contains(text)) {
            return FALSE;
        }
        return answer;
    }
}
