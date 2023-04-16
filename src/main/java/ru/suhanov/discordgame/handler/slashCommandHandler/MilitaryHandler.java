package ru.suhanov.discordgame.handler.slashCommandHandler;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.Util;
import ru.suhanov.discordgame.comand.Command;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.service.SpaceshipService;

@Service
public class MilitaryHandler extends AbstractSlashCommandHandler {
    private final SpaceshipService spaceshipService;

    @Autowired
    public MilitaryHandler(SpaceshipService spaceshipService) {
        this.spaceshipService = spaceshipService;
    }

    @Override
    protected void initHandler() {
        addCommand(new Command<>("create_spaceship", (event) -> {
            OptionMapping title = event.getOption("title");
            if (Util.allOptionsHasValue(title)) {
                try {
                    spaceshipService.createSpaceship(event.getMember().getIdLong(), title.getAsString());
                    event.reply("Корабль " + title.getAsString() + " успешно создан!").queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            } else {
                event.reply("Ошибка ввода данных!").queue();
            }
        }));
    }
}
