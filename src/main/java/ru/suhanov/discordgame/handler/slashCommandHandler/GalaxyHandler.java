package ru.suhanov.discordgame.handler.slashCommandHandler;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.Util;
import ru.suhanov.discordgame.comand.Command;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.exception.JDAException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.MessageWithButtons;
import ru.suhanov.discordgame.service.GalaxyService;
import ru.suhanov.discordgame.service.GameUserService;
import ru.suhanov.discordgame.service.ModService;
import ru.suhanov.discordgame.service.SendService;

import java.io.File;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GalaxyHandler extends AbstractSlashCommandHandler {
    private final GalaxyService galaxyService;
    private final ModService modService;
    private final SendService sendService;

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        switch (event.getModalId()) {
            case "addModifierToGalaxy" -> {
                String modTitle = event.getValue("modTitle").getAsString();
                String galaxyTitle = event.getValue("galaxyTitle").getAsString();

                try {
                    modService.addModForGalaxy(modTitle, galaxyTitle);
                    sendService.sendMessageToPersonalChannel(event, "Модификатор " + modTitle
                            + " успешно добавлен в галлактику " + galaxyTitle);
                } catch (DataBaseException | JDAException e) {
                    event.reply(e.getMessage()).queue();
                }
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().contains("galaxyInfo:")) {
            String title = event.getComponentId().replace("galaxyInfo:", "");
            try {
                MessageWithButtons message = galaxyService.galaxyToString(title, event.getMember().getIdLong());
                if (!message.getButtons().isEmpty()) {
                    sendService.sendMessageToPersonalChannel(event, message.getMessage(),
                            message.getButtons());
                } else {
                    event.reply(message.getMessage()).queue();
                }
            } catch (DataBaseException | JDAException e) {
                event.reply(e.getMessage()).queue();
            }
        } else if (event.getComponentId().contains("moveTo:")) {
            try {
                String title = event.getComponentId().replace("moveTo:", "");
                galaxyService.moveTo(title, event.getMember().getIdLong());
                sendService.sendMessageToPersonalChannel(event,
                        "Вы успешно переместились в галактику " + title + "!");
            } catch (DataBaseException | JDAException e) {
                event.reply(e.getMessage()).queue();
            }
        }
    }

    @Override
    protected void initHandler() {
        addCommand(new Command<>("map", event -> {
            MessageWithButtons message = galaxyService.getMap();

            if (!message.getButtons().isEmpty()) {
                try {
                    sendService.sendMessageToPersonalChannel(event,
                            message.getUrl() + Util.getFormatString(message.getMessage()),
                            message.getButtons());
                } catch (DataBaseException | JDAException e) {
                    event.reply(e.getMessage()).queue();
                }
            } else
                event.reply("Карта пуста!").queue();
        }));


        addCommand(new Command<>("create_galaxy", event -> {
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
                    sendService.sendMessageToPersonalChannel(event,
                            "Галактика " + title.getAsString() + " успешно создана!");
                } catch (DataBaseException | JDAException e) {
                    event.reply(e.getMessage()).queue();
                }
            } else {
                event.reply("Ошибка ввода данных!").queue();
            }
        }));

        addCommand(new Command<>("add_modifier_to_galaxy", event -> {
            TextInput modTitle = TextInput.create("modTitle", "Название модификатора", TextInputStyle.SHORT)
                    .setPlaceholder("Введите название модификатора...")
                    .build();

            TextInput galaxyTitle = TextInput.create("galaxyTitle", "Название галактики", TextInputStyle.SHORT)
                    .setPlaceholder("Введите название галактики...")
                    .build();

            Modal modal = Modal.create("addModifierToGalaxy", "Окно добавления модификатора для галактики")
                    .addComponents(
                            ActionRow.of(modTitle),
                            ActionRow.of(galaxyTitle))
                    .build();

            event.replyModal(modal).queue();
        }));
    }
}
