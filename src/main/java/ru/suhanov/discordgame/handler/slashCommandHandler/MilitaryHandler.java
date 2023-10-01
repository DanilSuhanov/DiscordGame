package ru.suhanov.discordgame.handler.slashCommandHandler;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.MessageWithButtons;
import ru.suhanov.discordgame.model.military.FleetType;
import ru.suhanov.discordgame.service.SpaceshipService;

@Service
public class MilitaryHandler extends AbstractSlashCommandHandler {
    private final SpaceshipService spaceshipService;

    @Autowired
    public MilitaryHandler(SpaceshipService spaceshipService) {
        this.spaceshipService = spaceshipService;
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
                }
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().contains("spaceshipInfo:")) {
            String title = event.getComponentId().replace("spaceshipInfo:", "");
            System.out.println(title);
        }

        switch (event.getComponentId()) {
            case "military_info" -> {
                try {
                    MessageWithButtons message = spaceshipService.getFleetInfo(event.getMember().getIdLong());
                    event.reply(message.getMessage()).addActionRow(message.getButtons()).queue();
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

                StringSelectMenu stringSelectMenu = StringSelectMenu.create("spaceshipType")
                        .addOption("SMALL", "SMALL")
                        .addOption("MEDIUM", "MEDIUM")
                        .addOption("LARGE", "LARGE")
                        .build();



                Modal modal = Modal.create("createSpaceshipMod", "Окно создания корабля")
                        .addComponents(ActionRow.of(subject))
                        .build();

                event.replyModal(modal).queue();
            }
        }
    }

    @Override
    protected void initHandler() {

    }
}
