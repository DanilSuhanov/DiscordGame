package ru.suhanov.discordgame.handler;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.comand.Command;

import java.util.ArrayList;
import java.util.List;

@Service
public abstract class Handler<T extends Event> extends ListenerAdapter {

    protected List<Command<T>> commands = new ArrayList<>();

    @Autowired
    protected Handler() {
        initHandler();
    }

    protected abstract void initHandler();

    protected void addCommand(Command<T> command) {
        commands.add(command);
    }
}