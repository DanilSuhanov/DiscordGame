package ru.suhanov.discordgame;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;
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

    public static StringBuilder listToStringList(String title, List<?> data) {
        if (data.isEmpty())
            return new StringBuilder("Список пуст!");
        StringBuilder stringBuilder = new StringBuilder().append(title);
        data.forEach(o -> stringBuilder.append("\n").append(o.toString()));
        return stringBuilder;
    }

    public static StringBuilder listToStringList(String title, List<?> data, StringBuilder stringBuilder) {
        stringBuilder.append(title);
        data.forEach(o -> stringBuilder.append("\n").append(o.toString()));
        return stringBuilder;
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

    public static String getFormatString(String str) {
        return "\n```\n" + str + "```";
    }
}
