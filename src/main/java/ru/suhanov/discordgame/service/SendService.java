package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.exception.JDAException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SendService {
    private final GameUserService gameUserService;

    public <T extends GenericInteractionCreateEvent> void sendMessageToPersonalChannel(@NotNull T event,
                                                                                       String message) throws DataBaseException, JDAException {
        String memberName = gameUserService.findGameUserByDiscordId(event.getMember().getIdLong()).getName();
        getPersonalChannel(memberName, event).sendMessage(message).queue();
    }

    public <T extends GenericInteractionCreateEvent> void sendMessageToPersonalChannel(@NotNull T event,
                                                                                       String message,
                                                                                       ActionComponent ... actionComponents) throws DataBaseException, JDAException {
        String memberName = gameUserService.findGameUserByDiscordId(event.getMember().getIdLong()).getName();
        MessageCreateAction send = getPersonalChannel(memberName, event).sendMessage(message);
        for (ActionComponent actionComponent : actionComponents) {
            send.addActionRow(actionComponent);
        }
        send.queue();
    }

    private TextChannel getPersonalChannel(String memberName, @NotNull GenericInteractionCreateEvent event) throws JDAException {
        TextChannel channel = findPersonalTextChannel(memberName, event);
        if (channel == null) {
            event.getGuild().createTextChannel(memberName).queue();
            throw new JDAException("Канал не найден, создаю!");
        }
        return channel;
    }

    private TextChannel findPersonalTextChannel(String memberName, @NotNull GenericInteractionCreateEvent event) throws JDAException {
        return event.getGuild().getTextChannels().stream()
                .filter(guildChannel -> guildChannel.getName().equals(memberName.toLowerCase()))
                .findFirst().orElse(null);
    }
}
