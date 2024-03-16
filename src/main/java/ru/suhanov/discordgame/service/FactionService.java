package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.exception.StepException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.invite.InviteToFaction;
import ru.suhanov.discordgame.model.union.Faction;
import ru.suhanov.discordgame.repository.FactionRepository;
import ru.suhanov.discordgame.repository.InviteToFactionRepository;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FactionService {
    private final FactionRepository factionRepository;
    private final GameUserService gameUserService;
    private final InviteToFactionRepository inviteToFactionRepository;
    private final StepService stepService;

    private boolean playerWithoutFaction(GameUser gameUser) {
        return gameUser.getFaction() == null;
    }

    public void createFaction(String title, String description, long leaderId) throws DataBaseException, StepException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(leaderId);
        stepService.checkStep(gameUser.getName());

        Faction faction = new Faction(title, description, gameUser);
        if (!factionRepository.existsFactionByTitle(faction.getTitle())) {
            if (playerWithoutFaction(faction.getLeader())) {
                factionRepository.save(faction);
                gameUser.setFaction(faction);
                gameUserService.save(gameUser);
            } else
                throw new DataBaseException("Вы уже состоите в другой фракции!");
        } else
            throw new DataBaseException("Фракция с таким названием уже существует!");
    }

    public void inviteUser(long leaderId, String memberName) throws DataBaseException, StepException {
        GameUser member = gameUserService.findGameUserByName(memberName);
        stepService.checkStep(memberName);

        if (member.getFaction() != null) {
            throw new DataBaseException("Пользователь уже состоит в фракции!");
        }
        GameUser leader = gameUserService.findGameUserByDiscordId(leaderId);
        InviteToFaction inviteToFaction = new InviteToFaction(leader, member);
        inviteToFactionRepository.save(inviteToFaction);
    }

    public List<String> getInvites(long id) throws DataBaseException {
        List<InviteToFaction> invites = inviteToFactionRepository.findInviteToFactionsByTo_DiscordId(id);
        List<String> result = new LinkedList<>();
        if (invites.isEmpty()) {
            throw new DataBaseException("Список пуст!");
        }
        StringBuilder stringBuilder = new StringBuilder("Список приглашений во фракции:\n");
        for (InviteToFaction invite : invites) {
            stringBuilder.append("Приглашение в фракцию - ").append(invite.getFrom().getFaction().getTitle())
                    .append("\n");
        }
        result.add(stringBuilder + "\n\nДля принятия приглашения во фракцию нажмите на название соответствующей фракции:");
        result.addAll(invites.stream().map(i -> i.getFrom().getFaction().getTitle()).toList());
        return result;
    }

    public List<Button> titlesToButtons(List<String> titles) {
        List<Button> buttons = new LinkedList<>();
        titles.forEach(title -> buttons.add(Button.primary("acceptInvite:" + title, title)));
        return buttons;
    }

    public InviteToFaction findInviteToFactionByFactionTitle(String title) throws DataBaseException {
        return inviteToFactionRepository.findInviteToFactionByFrom_Faction_Title(title)
                .orElseThrow(() -> new DataBaseException("Приглашение не найдено!"));
    }

    public void acceptInvitation(String title, long id) throws DataBaseException, StepException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(id);
        stepService.checkStep(gameUser.getName());

        InviteToFaction inviteToFaction = findInviteToFactionByFactionTitle(title);
        Faction faction = inviteToFaction.getFrom().getFaction();
        gameUser.setFaction(faction);

        gameUserService.save(gameUser);
        inviteToFactionRepository.delete(inviteToFaction);
    }

    public String getFactionInfo(long id) throws DataBaseException {
        return gameUserService.findGameUserByDiscordId(id).getFaction().toString();
    }
}
