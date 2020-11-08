package net.starype.quiz.api.game.answer;

public interface AnswerParser {

    Answer process(Answer str);

    default Answer process(String str) {
        return process(Answer.fromString(str));
    }

}
