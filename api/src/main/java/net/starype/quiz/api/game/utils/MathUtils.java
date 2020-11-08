package net.starype.quiz.api.game.utils;

public class MathUtils {

    public static int Clamp(int value, int min, int max) {
        return Math.max(Math.min(value, max), min);
    }

    public static double Clamp(double value, double min, double max) {
        return Math.max(Math.min(value, max), min);
    }

    public static int Clamp01(int value) {
        return Clamp(value, 0, 1);
    }

    public static double Clamp01(double value) {
        return Clamp(value, 0.0, 1.0);
    }
}
