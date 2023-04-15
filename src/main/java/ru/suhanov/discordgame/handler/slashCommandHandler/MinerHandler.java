package ru.suhanov.discordgame.handler.slashCommandHandler;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.Util;
import ru.suhanov.discordgame.comand.Command;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.Galaxy;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.miner.OilMiner;
import ru.suhanov.discordgame.service.GalaxyService;
import ru.suhanov.discordgame.service.GameUserService;
import ru.suhanov.discordgame.service.MinerService;

@Service
public class MinerHandler extends AbstractSlashCommandHandler {
    private final MinerService minerService;
    private final GameUserService gameUserService;
    private final GalaxyService galaxyService;

    @Autowired
    public MinerHandler(MinerService minerService, GameUserService gameUserService, GalaxyService galaxyService) {
        this.minerService = minerService;
        this.gameUserService = gameUserService;
        this.galaxyService = galaxyService;
    }

    @Override
    protected void initHandler() {
        addCommand(new Command<>("create_oil_miner", (event) -> {
            OptionMapping title = event.getOption("title");
            if (Util.allOptionsHasValue(title)) {
                try {
                    GameUser gameUser = gameUserService.findGameUserByDiscordId(event.getMember().getIdLong());
                    OilMiner oilMiner = new OilMiner();
                    oilMiner.setTitle(title.getAsString());
                    minerService.newMiner(gameUser, oilMiner);
                    event.reply("Топливный майнер " + oilMiner.getTitle() + " создан!").queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            } else {
                event.reply("Ошибка ввода данных!").queue();
            }
        }));
    }
}
