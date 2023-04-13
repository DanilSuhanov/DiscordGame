package ru.suhanov.discordgame;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class Util {
    private Util() {}

    public static boolean allOptionsNotNull(OptionMapping ... optionMappings) {
        for (OptionMapping option : optionMappings) {
            if (option == null)
                return false;
        }
        return true;
    }
}
