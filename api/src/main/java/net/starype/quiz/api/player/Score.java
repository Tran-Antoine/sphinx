package net.starype.quiz.api.player;

public class Score {

    private double points;

    private int answeredQuestions;
    private int correctAnsweredQuestions;

    public Score() {
        this.points = 0;
        this.answeredQuestions = 0;
        this.correctAnsweredQuestions = 0;
    }

    public double getAccuracy() {
        return (double)correctAnsweredQuestions / answeredQuestions;
    }

    public double getPoints() {
        return points;
    }

    public int getAnsweredQuestions() {
        return answeredQuestions;
    }

    public int getCorrectAnsweredQuestions() {
        return correctAnsweredQuestions;
    }

    public void incrementScore(double increment) {
        this.points += increment;
    }

    public void incrementAnsweredQuestionCount(boolean isAnswerCorrect) {
        this.answeredQuestions++;
        if(isAnswerCorrect) {
            this.correctAnsweredQuestions++;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(points);
    }
}
