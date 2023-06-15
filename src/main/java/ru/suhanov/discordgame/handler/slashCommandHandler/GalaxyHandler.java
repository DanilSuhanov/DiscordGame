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

import java.util.List;

@Service
public class GalaxyHandler extends AbstractSlashCommandHandler {
    private final GalaxyService galaxyService;
    private final GameUserService gameUserService;

    @Autowired
    protected GalaxyHandler(GalaxyService galaxyService, GameUserService gameUserService) {
        this.galaxyService = galaxyService;
        this.gameUserService = gameUserService;
    }

    @Override
    protected void initHandler() {
        addCommand(new Command<>("map", (event) -> {

        }));


        addCommand(new Command<>("create_galaxy", (event) -> {
            OptionMapping title = event.getOption("title");
            OptionMapping size = event.getOption("size");
            OptionMapping neighbors = event.getOption("neighbors");

            if (Util.allOptionsHasValue(title, size)) {
                try {
                    if (neighbors == null) {
                        galaxyService.newGalaxy(title.getAsString(), size.getAsInt());
                    } else {
                        galaxyService.newGalaxy(title.getAsString(), size.getAsInt(),
                                List.of(neighbors.getAsString().split(" ")));
                    }
                    event.reply("Галактика " + title.getAsString() + " успешно создана!").queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            } else {
                event.reply("Ошибка ввода данных!").queue();
            }
        }));

        addCommand(new Command<>("move_to", (event) -> {
            OptionMapping galaxyName = event.getOption("galaxy");
            if (Util.allOptionsHasValue(galaxyName)) {
                try {
                    GameUser gameUser = gameUserService.findGameUserByDiscordId(event.getMember().getIdLong());
                    galaxyService.moveTo(galaxyName.getAsString(), event.getMember().getIdLong());
                    event.reply(gameUser.getName() + " перешёл в галактику " + galaxyName.getAsString()).queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            } else {
                event.reply("Ошибка ввода данных!").queue();
            }
        }));

        addCommand(new Command<>("galaxy_info", (event) -> {
            try {
                String result = galaxyService.getString(event.getMember().getIdLong());
                event.reply(result).queue();
            } catch (DataBaseException e) {
                event.reply(e.getMessage()).queue();
            }
        }));
    }
}
