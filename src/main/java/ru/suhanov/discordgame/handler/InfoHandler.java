package ru.suhanov.discordgame.handler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.comand.TextCommand;

import java.util.ArrayList;
import java.util.List;

@Service
public class InfoHandler extends ListenerAdapter implements SlashCommandHandler {

    private final JDA jda;
    private final List<TextCommand> textCommands = new ArrayList<>();

    @Autowired
    public InfoHandler(JDA jda) {
        jda.addEventListener(this);
        jda.updateCommands().addCommands(Commands.slash("profile", "Get profile info")).queue();
        this.jda = jda;
        initCommands();
    }

    private void initCommands() {
        textCommands.add(new TextCommand("profile", (event -> {
            event.reply("test").queue();
        })));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        textCommands.stream().filter(com -> com.getText().equals(event.getName()))
                .findFirst().ifPresent(command -> command.execute(event));
    }
}
