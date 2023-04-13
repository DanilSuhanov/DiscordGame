package ru.suhanov.discordgame.comand;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.events.Event;

import java.util.function.Consumer;

@AllArgsConstructor
@Data
public class Command<T extends Event> {
    private String text;

    private Consumer<T> action;

    public void execute(T event) {
        action.accept(event);
    }
}
