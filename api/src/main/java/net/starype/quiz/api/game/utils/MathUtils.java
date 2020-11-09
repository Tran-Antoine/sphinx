package net.starype.quiz.api.game.utils;

public class MathUtils {

    public static int clamp(int value, int min, int max) {
        return Math.max(Math.min(value, max), min);
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(Math.min(value, max), min);
    }

    public static int clamp01(int value) {
        return clamp(value, 0, 1);
    }

    public static double clamp01(double value) {
        return clamp(value, 0.0, 1.0);
    }
}
