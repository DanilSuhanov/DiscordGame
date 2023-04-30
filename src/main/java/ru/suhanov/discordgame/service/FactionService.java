package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.union.Faction;
import ru.suhanov.discordgame.repository.FactionRepository;

@Service
@Transactional
public class FactionService {
    private final FactionRepository factionRepository;
    private final GameUserService gameUserService;

    @Autowired
    public FactionService(FactionRepository factionRepository, GameUserService gameUserService) {
        this.factionRepository = factionRepository;
        this.gameUserService = gameUserService;
    }

    private boolean playerWithoutFaction(GameUser gameUser) {
        return gameUser.getFaction() == null;
    }

    public void createFaction(String title, String description, Long leaderId) throws DataBaseException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(leaderId);
        Faction faction = new Faction(title, description, gameUser);

        if (!factionRepository.existsFactionByTitle(faction.getTitle())) {
            if (playerWithoutFaction(faction.getLeader())) {
                factionRepository.save(faction);
            } else
                throw new DataBaseException("Лидер фракции уже состоит в другой фракции!");
        } else
            throw new DataBaseException("Фракция с таким названием уже существует!");
    }
}
