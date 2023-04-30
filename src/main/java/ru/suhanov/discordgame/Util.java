package ru.suhanov.discordgame;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Random;

public class Util {
    private Util() {}

    public static boolean allOptionsHasValue(OptionMapping ... optionMappings) {
        for (OptionMapping option : optionMappings) {
            if (option == null)
                return false;
        }
        return true;
    }

    public static int getRandomFromTo(int from, int to) {
        return new Random().nextInt(to - from + 1) + from;
    }

    public static int getSection(int x) {
        return String.valueOf(x).length();
    }

    public static String getStingCount(int count) {
        return " ".repeat(count);
    }
}
