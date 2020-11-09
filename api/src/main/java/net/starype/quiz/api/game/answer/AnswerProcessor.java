package net.starype.quiz.api.game.answer;

public interface AnswerProcessor {

    Answer process(Answer answer);

    default Answer process(String str) {
        return process(Answer.fromString(str));
    }

    default AnswerProcessor combine(AnswerProcessor processor) {
        return (answer) -> this.process(processor.process(answer));
    }

}
