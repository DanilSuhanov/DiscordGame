package ru.suhanov.discordgame.handler;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommandHandler {
    void onSlashCommandInteraction(SlashCommandInteractionEvent event);
}
