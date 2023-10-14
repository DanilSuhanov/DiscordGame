package ru.suhanov.discordgame.handler.slashCommandHandler;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.handler.Handler;
import ru.suhanov.discordgame.handler.SlashCommandHandler;

@Service
public abstract class AbstractSlashCommandHandler extends Handler<SlashCommandInteractionEvent> implements SlashCommandHandler {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commands.stream().filter(com -> com.getText().equals(event.getName()))
                .findFirst().ifPresent(command -> {
                    command.execute(event);
                    event.reply("Команда выполняется...").queue();
                });
    }
}
