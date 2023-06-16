package ru.suhanov.discordgame.handler.slashCommandHandler;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.Util;
import ru.suhanov.discordgame.comand.Command;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.MessageWithButtons;
import ru.suhanov.discordgame.model.miner.MetalMiner;
import ru.suhanov.discordgame.model.miner.Miner;
import ru.suhanov.discordgame.model.miner.OilMiner;
import ru.suhanov.discordgame.service.FactionService;
import ru.suhanov.discordgame.service.GameUserService;
import ru.suhanov.discordgame.service.MinerService;

import java.util.List;

@Service
public class AccountHandler extends AbstractSlashCommandHandler {
    private final GameUserService gameUserService;
    private final FactionService factionService;
    private final MinerService minerService;

    @Autowired
    public AccountHandler(GameUserService gameUserService, FactionService factionService, MinerService minerService) {
        this.gameUserService = gameUserService;
        this.factionService = factionService;
        this.minerService = minerService;
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        switch (event.getModalId()) {
            case "createFactionMod" -> {
                String factionTitle = event.getValue("factionTitle").getAsString();
                String factionDescription = event.getValue("factionDescription").getAsString();
                try {
                    factionService.createFaction(factionTitle, factionDescription,
                            event.getMember().getIdLong());
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
                event.reply("Фракция " + factionTitle + " успешно создана!").queue();
            }
            case "inviteToFactionMod" -> {
                String memberName = event.getValue("memberName").getAsString();
                try {
                    factionService.inviteUser(event.getMember().getIdLong(), memberName);
                    event.reply("Пользователь успешно приглашён!").queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            }
            case "createOilMinerMod" -> createMiner(new OilMiner(), event);
            case "createMetalMinerMod" -> createMiner(new MetalMiner(), event);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().contains("acceptInvite:")) {
            String title = event.getComponentId().replace("acceptInvite:", "");
            try {
                factionService.acceptInvitation(title, event.getMember().getIdLong());
                event.reply("Приглашение во фракцию " + title + " принято!").queue();
            } catch (DataBaseException e) {
                event.reply(e.getMessage()).queue();
            }
        } else if (event.getComponentId().contains("workMiner:")) {
            try {
                String title = event.getComponentId().replace("workMiner:", "");
                event.reply(minerService.workMiner(event.getMember().getIdLong(), title)).queue();
            } catch (DataBaseException e) {
                event.reply(e.getMessage()).queue();
            }
        }

        switch (event.getComponentId()) {
            case "check_invitations" -> {
                try {
                    List<String> res = factionService.getInvites(event.getMember().getIdLong());
                    event.reply(Util.getFormatString(res.get(0))).addActionRow(
                            factionService.titlesToButtons(res.subList(1, res.size()))
                    ).queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            }
            case "create_faction" -> {
                TextInput subject = TextInput.create("factionTitle", "Название фракции", TextInputStyle.SHORT)
                        .setPlaceholder("Введите название фракции...")
                        .setMinLength(3)
                        .setMaxLength(30)
                        .build();

                TextInput body = TextInput.create("factionDescription", "Описание фракции", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Введите описание фракции...")
                        .setMinLength(5)
                        .setMaxLength(100)
                        .build();

                Modal modal = Modal.create("createFactionMod", "Окно создание фракции")
                        .addComponents(ActionRow.of(subject), ActionRow.of(body))
                        .build();

                event.replyModal(modal).queue();
            }
            case "invite_to_faction" -> {
                TextInput subject = TextInput.create("memberName", "Имя пользователя", TextInputStyle.SHORT)
                        .setPlaceholder("Введите имя пользователя...")
                        .setMinLength(3)
                        .setMaxLength(30)
                        .build();

                Modal modal = Modal.create("inviteToFactionMod", "Окно приглашения во фракцию")
                        .addComponents(ActionRow.of(subject))
                        .build();

                event.replyModal(modal).queue();
            }
            case "miners_info" -> {
                try {
                    MessageWithButtons message = gameUserService.getMinersInfo(event.getMember().getIdLong());
                    event.reply(Util.getFormatString(message.getMessage()))
                            .addActionRow(message.getButtons()).queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            }
            case "workAllMiners" -> {
                try {
                    event.reply(minerService.workAll(event.getMember().getIdLong())).queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            }
            case "create_oil_miner" -> {
                TextInput subject = TextInput.create("minerTitle", "Название топливного майнера", TextInputStyle.SHORT)
                        .setPlaceholder("Введите название топливного майнера...")
                        .setMinLength(3)
                        .setMaxLength(30)
                        .build();

                Modal modal = Modal.create("createOilMinerMod", "Окно создания топлиного майнера")
                        .addComponents(ActionRow.of(subject))
                        .build();

                event.replyModal(modal).queue();
            }
            case "create_metal_miner" -> {
                TextInput subject = TextInput.create("minerTitle", "Название майнера метала", TextInputStyle.SHORT)
                        .setPlaceholder("Введите название майнера метала...")
                        .setMinLength(3)
                        .setMaxLength(30)
                        .build();

                Modal modal = Modal.create("createMetalMinerMod", "Окно создания майнера метала")
                        .addComponents(ActionRow.of(subject))
                        .build();

                event.replyModal(modal).queue();
            }
        }
    }

    @Override
    protected void initHandler() {
        addCommand(new Command<>("profile", (event -> {
            try {
                String res = gameUserService.getString(event.getMember().getIdLong());
                event.reply(Util.getFormatString(res)).addActionRow(
                        Button.primary("check_invitations", "Проверить приглашения в фракцию"),
                        Button.primary("create_faction", "Создать новую фракцию"),
                        Button.primary("miners_info", "Информация о майнерах"),
                        Button.primary("military_info", "Информация о флоте")
                ).queue();
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

        addCommand(new Command<>("faction_info", (event) -> {
            try {
                String res = factionService.getFactionInfo(event.getMember().getIdLong());
                event.reply(Util.getFormatString(res)).addActionRow(
                        Button.primary("invite_to_faction", "Invite to Faction")
                ).queue();
            } catch (DataBaseException e) {
                event.reply(e.getMessage()).queue();
            }
        }));
    }

    private <T extends Miner> void createMiner(T miner, ModalInteractionEvent event) {
        String title = event.getValue("minerTitle").getAsString();
        try {
            miner.setTitle(title);
            minerService.newMiner(event.getMember().getIdLong(), miner);
            event.reply("Майнер " + miner.getTitle() + " создан!").queue();
        } catch (DataBaseException e) {
            event.reply(e.getMessage()).queue();
        }
    }
}
