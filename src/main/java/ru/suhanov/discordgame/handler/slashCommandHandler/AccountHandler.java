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

        addCommand(new Command<>("invite_to_faction", (event) -> {
            OptionMapping name = event.getOption("name");
            if (Util.allOptionsHasValue(name)) {
                try {
                    factionService.inviteUser(event.getMember().getIdLong(), name.getAsString());
                    event.reply("Пользователь успешно приглашён!").queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            }
        }));

        addCommand(new Command<>("check_invitations", (event) -> {
            String res = factionService.getInvites(event.getMember().getIdLong());
            event.reply(res).queue();
        }));

        addCommand(new Command<>("accept_invitation", (event) -> {
            OptionMapping title = event.getOption("title");
            if (Util.allOptionsHasValue(title)) {
                try {
                    factionService.acceptInvitation(title.getAsString(), event.getMember().getIdLong());
                    event.reply("Приглашение во фракцию " + title.getAsString() + " принято!").queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            }
        }));
    }
}
