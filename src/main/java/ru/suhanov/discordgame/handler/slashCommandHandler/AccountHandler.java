package ru.suhanov.discordgame.handler.slashCommandHandler;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.Util;
import ru.suhanov.discordgame.comand.Command;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.service.GalaxyService;
import ru.suhanov.discordgame.service.GameUserService;

@Service
public class AccountHandler extends AbstractSlashCommandHandler {
    private final GameUserService gameUserService;
    private final GalaxyService galaxyService;

    @Autowired
    public AccountHandler(GameUserService gameUserService, GalaxyService galaxyService) {
        this.gameUserService = gameUserService;
        this.galaxyService = galaxyService;
    }

    @Override
    protected void initHandler() {
        addCommand(new Command<>("profile", (event -> {
            try {
                GameUser gameUser = gameUserService.findGameUserByDiscordId(event.getMember().getIdLong());
                event.reply("Имя - " + gameUser.getName()
                        + "\nДеньги - " + gameUser.getMoney()
                        + "\nТопливо - " + gameUser.getOil()
                        + "\nТекущая галактика - " + gameUser.getLocation().getTitle()).queue();
            } catch (DataBaseException e) {
                event.reply(e.getMessage()).queue();
            }
        })));

        addCommand(new Command<>("registration", (event) -> {
            OptionMapping name = event.getOption("name");
            if (Util.allOptionsHasValue(name)) {
                try {
                    GameUser gameUser = new GameUser(name.getAsString(), event.getMember().getIdLong(), 0L,
                            galaxyService.findStarterGalaxy(), 1);
                    gameUserService.newGameUser(gameUser);
                    event.reply("Пользователь - " + gameUser.getName() + " зарегистрирован!").queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            } else {
                event.reply("Ошибка ввода данных!").queue();
            }
        }));
    }
}
