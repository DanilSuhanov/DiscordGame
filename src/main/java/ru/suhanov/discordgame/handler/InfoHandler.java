package ru.suhanov.discordgame.handler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.Util;
import ru.suhanov.discordgame.comand.Command;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.service.GameUserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class InfoHandler extends ListenerAdapter implements SlashCommandHandler {

    private final JDA jda;
    private final List<Command<SlashCommandInteractionEvent>> commands = new ArrayList<>();
    private final GameUserService gameUserService;

    @Autowired
    public InfoHandler(JDA jda, GameUserService gameUserService) {
        this.gameUserService = gameUserService;
        jda.addEventListener(this);
        jda.updateCommands().addCommands(
                Commands.slash("profile", "Get profile info"),
                Commands.slash("registration", "Create game user")
                        .addOption(OptionType.STRING, "name", "username")
        ).queue();
        this.jda = jda;
        initCommands();
    }

    private void initCommands() {
        commands.add(new Command<>("profile", (event -> {
            try {
                GameUser gameUser = gameUserService.findGameUserByDiscordId(event.getMember().getIdLong());
                event.reply("Имя - " + gameUser.getName()
                        + "\nДеньги - " + gameUser.getMoney()).queue();
            } catch (Exception e) {
                event.reply(e.getMessage()).queue();
            }
        })));

        commands.add(new Command<>("registration", (event) -> {
            OptionMapping name = event.getOption("name");
            if (Util.allOptionsNotNull(name)) {
                GameUser gameUser = new GameUser(name.getAsString(), event.getMember().getIdLong(), 0L);
                try {
                    gameUserService.newGameUser(gameUser);
                    event.reply("Пользователь - " + gameUser.getName() + " зарегистрирован!").queue();
                } catch (Exception e) {
                    event.reply(e.getMessage()).queue();
                }
            } else {
                event.reply("Ошибка ввода данных!").queue();
            }
        }));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commands.stream().filter(com -> com.getText().equals(event.getName()))
                .findFirst().ifPresent(command -> command.execute(event));
    }
}
