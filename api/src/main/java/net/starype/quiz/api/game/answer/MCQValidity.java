package net.starype.quiz.api.game.answer;

public class MCQValidity implements ValidityEvaluator {

    @Override
    public boolean isValid(Answer answer) {
        return answer
                .split(";")
                .stream()
                .allMatch(splitAnswer -> {
                    String text = splitAnswer.getAnswerText();
                    return text.length() == 1 && Character.isLetter(text.charAt(0));
                });
    }
}
