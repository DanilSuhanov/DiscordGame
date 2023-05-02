package ru.suhanov.discordgame.handler.slashCommandHandler;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.Util;
import ru.suhanov.discordgame.comand.Command;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.Galaxy;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.miner.MetalMiner;
import ru.suhanov.discordgame.model.miner.Miner;
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
            createMiner(new OilMiner(), event);
        }));
        addCommand(new Command<>("create_metal_miner", (event) -> {
            createMiner(new MetalMiner(), event);
        }));

        addCommand(new Command<>("launch_all_miners", (event) -> {
            try {
                event.reply(minerService.workAll(event.getMember().getIdLong())).queue();
            } catch (DataBaseException e) {
                event.reply(e.getMessage()).queue();
            }
        }));
    }

    private <T extends Miner> void createMiner(T miner, SlashCommandInteraction event) {
        OptionMapping title = event.getOption("title");
        if (Util.allOptionsHasValue(title)) {
            try {
                miner.setTitle(title.getAsString());
                minerService.newMiner(event.getMember().getIdLong(), miner);
                event.reply("Майнер " + miner.getTitle() + " создан!").queue();
            } catch (DataBaseException e) {
                event.reply(e.getMessage()).queue();
            }
        } else {
            event.reply("Ошибка ввода данных!").queue();
        }
    }
}
