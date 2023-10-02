package ru.suhanov.discordgame.handler.slashCommandHandler;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.MessageWithItems;
import ru.suhanov.discordgame.model.military.FleetType;
import ru.suhanov.discordgame.service.GameUserService;
import ru.suhanov.discordgame.service.SpaceshipService;

@Service
public class MilitaryHandler extends AbstractSlashCommandHandler {

    private final SpaceshipService spaceshipService;
    private final GameUserService gameUserService;

    @Autowired
    public MilitaryHandler(SpaceshipService spaceshipService, GameUserService gameUserService) {
        this.spaceshipService = spaceshipService;
        this.gameUserService = gameUserService;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String type = event.getValues().get(0);
        if (type.contains("CREATE_FLEET_")) {
            try {
                FleetType fleetType = FleetType.valueOf(type.replace("CREATE_FLEET_", ""));
                String title = "Fleet" + (gameUserService.getCountOfFleet(event.getMember().getIdLong()) + 1);
                spaceshipService.createSpaceship(event.getMember().getIdLong(), title, fleetType);
                event.reply("Корабль " + title + " успешно создан!").queue();
            } catch (DataBaseException | IllegalArgumentException e) {
                event.reply(e.getMessage()).queue();
            }
        }
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        switch (event.getModalId()) {
            case "createSpaceshipMod" -> {
                String title = event.getValue("spaceshipTitle").getAsString();
                String type = event.getValue("spaceshipType").getAsString();
                try {
                    spaceshipService.createSpaceship(event.getMember().getIdLong(), title,
                            FleetType.valueOf(type));
                    event.reply("Корабль " + title + " успешно создан!").queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                } catch (IllegalArgumentException e) {
                    event.reply("Некорректный тип!").queue();
                }
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().contains("spaceshipInfo:")) {
            String title = event.getComponentId().replace("spaceshipInfo:", "");

        }

        switch (event.getComponentId()) {
            case "military_info" -> {
                try {
                    MessageWithItems message = spaceshipService.getFleetInfo(event.getMember().getIdLong());
                    event.reply(message.getMessage())
                            .addActionRow(message.getButtons())
                            .addActionRow(message.getSelectMenu())
                            .queue();
                } catch (DataBaseException e) {
                    event.reply(e.getMessage()).queue();
                }
            }
            case "createSpaceship" -> {
                TextInput subject = TextInput.create("spaceshipTitle", "Название корабля", TextInputStyle.SHORT)
                        .setPlaceholder("Введите название корабля...")
                        .setMinLength(3)
                        .setMaxLength(30)
                        .build();

                TextInput type = TextInput.create("spaceshipType", "Тип коробля", TextInputStyle.SHORT)
                        .setPlaceholder("Введите тип коробля: SMALL, MEDIUM, LARGE")
                        .build();

                Modal modal = Modal.create("createSpaceshipMod", "Окно создания корабля")
                        .addComponents(ActionRow.of(subject), ActionRow.of(type))
                        .build();

                event.replyModal(modal).queue();
            }
        }
    }

    @Override
    protected void initHandler() {

    }
}
