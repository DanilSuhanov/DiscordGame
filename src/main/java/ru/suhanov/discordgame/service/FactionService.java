package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.invite.InviteToFaction;
import ru.suhanov.discordgame.model.union.Faction;
import ru.suhanov.discordgame.repository.FactionRepository;
import ru.suhanov.discordgame.repository.InviteToFactionRepository;

import java.util.List;

@Service
@Transactional
public class FactionService {
    private final FactionRepository factionRepository;
    private final GameUserService gameUserService;
    private final InviteToFactionRepository inviteToFactionRepository;

    @Autowired
    public FactionService(FactionRepository factionRepository, GameUserService gameUserService, InviteToFactionRepository inviteToFactionRepository) {
        this.factionRepository = factionRepository;
        this.gameUserService = gameUserService;
        this.inviteToFactionRepository = inviteToFactionRepository;
    }

    private boolean playerWithoutFaction(GameUser gameUser) {
        return gameUser.getFaction() == null;
    }

    public void createFaction(String title, String description, long leaderId) throws DataBaseException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(leaderId);
        Faction faction = new Faction(title, description, gameUser);

        if (!factionRepository.existsFactionByTitle(faction.getTitle())) {
            if (playerWithoutFaction(faction.getLeader())) {
                factionRepository.save(faction);
                gameUser.setFaction(faction);
                gameUserService.save(gameUser);
            } else
                throw new DataBaseException("Лидер фракции уже состоит в другой фракции!");
        } else
            throw new DataBaseException("Фракция с таким названием уже существует!");
    }

    public void inviteUser(long leaderId, String memberName) throws DataBaseException {
        GameUser member = gameUserService.findGameUserByName(memberName);
        if (member.getFaction() != null) {
            throw new DataBaseException("Пользователь уже состоит в фракции!");
        }
        GameUser leader = gameUserService.findGameUserByDiscordId(leaderId);
        InviteToFaction inviteToFaction = new InviteToFaction(leader, member);
        inviteToFactionRepository.save(inviteToFaction);
    }

    public String getInvites(long id) {
        List<InviteToFaction> invites = inviteToFactionRepository.findInviteToFactionsByTo_DiscordId(id);
        if (invites.size() == 0) {
            return "Список пуст!";
        }
        StringBuilder stringBuilder = new StringBuilder("Список приглашений во фракции:\n");
        for (InviteToFaction invite : invites) {
            stringBuilder.append("Приглашение в фракцию - ").append(invite.getFrom().getFaction().getTitle())
                    .append("\n");
        }
        return stringBuilder.toString();
    }

    public InviteToFaction findInviteToFactionByFactionTitle(String title) throws DataBaseException {
        return inviteToFactionRepository.findInviteToFactionByFrom_Faction_Title(title)
                .orElseThrow(() -> new DataBaseException("Приглашение не найдено!"));
    }

    public void acceptInvitation(String title, long id) throws DataBaseException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(id);
        InviteToFaction inviteToFaction = findInviteToFactionByFactionTitle(title);
        Faction faction = inviteToFaction.getFrom().getFaction();
        gameUser.setFaction(faction);

        gameUserService.save(gameUser);
        inviteToFactionRepository.delete(inviteToFaction);
    }
}
