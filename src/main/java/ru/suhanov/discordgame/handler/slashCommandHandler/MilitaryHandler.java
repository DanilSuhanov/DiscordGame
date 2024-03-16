package ru.suhanov.discordgame.handler.slashCommandHandler;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.exception.JDAException;
import ru.suhanov.discordgame.model.MessageWithItems;
import ru.suhanov.discordgame.model.military.FleetType;
import ru.suhanov.discordgame.service.GameUserService;
import ru.suhanov.discordgame.service.SendService;
import ru.suhanov.discordgame.service.SpaceshipService;

@Service
@RequiredArgsConstructor
public class MilitaryHandler extends ListenerAdapter {
    private final SpaceshipService spaceshipService;
    private final GameUserService gameUserService;
    private final SendService sendService;

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String type = event.getValues().get(0);
        if (type.contains("CREATE_FLEET_")) {
            try {
                FleetType fleetType = FleetType.valueOf(type.replace("CREATE_FLEET_", ""));
                String title = "Fleet" + (gameUserService.getCountOfFleet(event.getMember().getIdLong()) + 1);
                spaceshipService.createSpaceship(event.getMember().getIdLong(), title, fleetType);
                sendService.sendMessageToPersonalChannel(event, "Корабль " + title + " успешно создан!");
            } catch (DataBaseException | IllegalArgumentException | JDAException e) {
                event.reply(e.getMessage()).queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().contains("spaceshipInfo:")) {
            String title = event.getComponentId().replace("spaceshipInfo:", "");
            try {
                String info = spaceshipService.getShipInfo(title);
                sendService.sendMessageToPersonalChannel(event, info);
            } catch (DataBaseException | JDAException e) {
                event.reply(e.getMessage()).queue();
            }
        }

        switch (event.getComponentId()) {
            case "military_info" -> {
                try {
                    MessageWithItems message = spaceshipService.getFleetInfo(event.getMember().getIdLong());
                    sendService.sendMessageToPersonalChannel(event,
                            message.getMessage(),
                            message.getButtons(),
                            message.getSelectMenu());
                } catch (DataBaseException | JDAException e) {
                    event.reply(e.getMessage()).queue();
                }
            }
            case "getTypeInfo" -> {
                event.reply(spaceshipService.getTypesInfo()).queue();
            }
        }
    }
}
