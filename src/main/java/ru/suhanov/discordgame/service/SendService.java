package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.exception.JDAException;

import java.util.EnumSet;
import java.util.List;

import static net.dv8tion.jda.api.Permission.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SendService {
    private final GameUserService gameUserService;

    public <T extends GenericInteractionCreateEvent> void sendMessageToPersonalChannel(@NotNull T event,
                                                                                       String message) throws DataBaseException, JDAException {
        String memberName = gameUserService.findGameUserByDiscordId(event.getMember().getIdLong()).getName();
        TextChannel personalChannel = getPersonalChannel(memberName, event);
        if (event.getChannel().getName().equals(personalChannel.getName()) && event instanceof IReplyCallback) {
            ((IReplyCallback) event).reply(message).queue();
        } else {
            personalChannel.sendMessage(message).queue();
        }
    }

    public <T extends GenericInteractionCreateEvent> void sendMessageToPersonalChannel(@NotNull T event,
                                                                                       String message,
                                                                                       ActionComponent ... actionComponents) throws DataBaseException, JDAException {
        String memberName = gameUserService.findGameUserByDiscordId(event.getMember().getIdLong()).getName();
        MessageCreateAction send = getPersonalChannel(memberName, event).sendMessage(message);
        for (var actionComponent : actionComponents) {
            send.addActionRow(actionComponent);
        }
        send.queue();
    }

    public <T extends GenericInteractionCreateEvent> void sendMessageToPersonalChannel(@NotNull T event,
                                                                                       String message,
                                                                                       List<Button> buttons,
                                                                                       ActionComponent ... actionComponents) throws DataBaseException, JDAException {
        String memberName = gameUserService.findGameUserByDiscordId(event.getMember().getIdLong()).getName();
        MessageCreateAction send = getPersonalChannel(memberName, event).sendMessage(message);
        send = buildMessageWithButtons(send, buttons);
        for (var actionComponent : actionComponents) {
            send.addActionRow(actionComponent);
        }
        send.queue();
    }

    private TextChannel getPersonalChannel(String memberName, @NotNull GenericInteractionCreateEvent event) {
        TextChannel channel = findPersonalTextChannel(memberName, event);
        if (channel == null)
            return createPersonalTextChannel(event.getMember(), memberName);
        return channel;
    }

    public TextChannel createPersonalTextChannel(Member member, String name) {
        Guild guild = member.getGuild();
        return guild.createTextChannel(name)
                .addPermissionOverride(member, EnumSet.of(VIEW_CHANNEL, MESSAGE_SEND, MESSAGE_HISTORY, MESSAGE_ADD_REACTION), null)
                .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(VIEW_CHANNEL))
                .complete();
    }

    private TextChannel findPersonalTextChannel(String memberName, @NotNull GenericInteractionCreateEvent event) {
        return event.getGuild().getTextChannels().stream()
                .filter(guildChannel -> guildChannel.getName().equals(memberName.toLowerCase()))
                .findFirst().orElse(null);
    }

    public ReplyCallbackAction buildMessageWithButtons(ReplyCallbackAction replyCallbackAction, List<Button> buttons) {
        for (int i = 0; i < buttons.size(); i+=5) {
            if (i+5 > buttons.size()) {
                replyCallbackAction.addActionRow(buttons.subList(i, buttons.size()));
            } else {
                replyCallbackAction.addActionRow(buttons.subList(i, i + 5));
            }
        }
        return replyCallbackAction;
    }

    public MessageCreateAction buildMessageWithButtons(MessageCreateAction messageCreateRequest, List<Button> buttons) {
        for (int i = 0; i < buttons.size(); i+=5) {
            if (i+5 > buttons.size()) {
                messageCreateRequest.addActionRow(buttons.subList(i, buttons.size()));
            } else {
                messageCreateRequest.addActionRow(buttons.subList(i, i + 5));
            }
        }
        return messageCreateRequest;
    }
}
