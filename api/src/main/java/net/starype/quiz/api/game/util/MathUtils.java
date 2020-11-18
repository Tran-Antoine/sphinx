package net.starype.quiz.api.game.util;

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

    public static double lerp(double value, double inMin, double inMax, double outMin, double outMax)  {
        return ((value - inMin) / (inMin - inMax)) *  (outMin - outMax) + outMin;
    }

    public static double clampedLerp(double value, double inMin, double inMax, double outMin, double outMax) {
        return clamp(lerp(value, inMin, inMax, outMin, outMax), outMin, outMax);
    }

    public static double lerp01(double value, double inMin, double inMax)  {
        return lerp(value, inMin, inMax, 0.0,1.0);
    }

    public static double clampedLerp01(double value, double inMin, double inMax) {
        return clampedLerp(value, inMin, inMax, 0.0,1.0);
    }
}
