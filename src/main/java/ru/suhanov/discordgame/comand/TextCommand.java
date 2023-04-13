package ru.suhanov.discordgame.comand;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.function.Consumer;

@AllArgsConstructor
@Data
public class TextCommand {
    private String text;

    private Consumer<SlashCommandInteractionEvent> action;

    public void execute(SlashCommandInteractionEvent event) {
        action.accept(event);
    }
}
