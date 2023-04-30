package ru.suhanov.discordgame.handler.slashCommandHandler;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.Util;
import ru.suhanov.discordgame.comand.Command;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.union.Faction;
import ru.suhanov.discordgame.service.FactionService;
import ru.suhanov.discordgame.service.GalaxyService;
import ru.suhanov.discordgame.service.GameUserService;

@Service
public class AccountHandler extends AbstractSlashCommandHandler {
    private final GameUserService gameUserService;
    private final GalaxyService galaxyService;
    private final FactionService factionService;

    @Autowired
    public AccountHandler(GameUserService gameUserService, GalaxyService galaxyService, FactionService factionService) {
        this.gameUserService = gameUserService;
        this.galaxyService = galaxyService;
        this.factionService = factionService;
    }

    @Override
    protected void initHandler() {
        addCommand(new Command<>("profile", (event -> {
            try {
                String res = gameUserService.getString(event.getMember().getIdLong());
                event.reply(res).queue();
            } catch (DataBaseException e) {
                event.reply(e.getMessage()).queue();
            }
        })));

        addCommand(new Command<>("registration", (event) -> {
            OptionMapping name = event.getOption("name");
            if (Util.allOptionsHasValue(name)) {
                try {
                    gameUserService.newGameUser(name.getAsString(), event.getMember().getIdLong());
                    event.reply("Пользователь - " + name.getAsString() + " зарегистрирован!").queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            } else {
                event.reply("Ошибка ввода данных!").queue();
            }
        }));

        addCommand(new Command<>("create_faction", (event) -> {
            OptionMapping title = event.getOption("title");
            OptionMapping description = event.getOption("description");
            if (Util.allOptionsHasValue(title, description)) {
                try {
                    factionService.createFaction(title.getAsString(), description.getAsString(),
                            event.getMember().getIdLong());

                    event.reply("Фракция " + title.getAsString() + " успешно создана!").queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            }
        }));
    }
}
