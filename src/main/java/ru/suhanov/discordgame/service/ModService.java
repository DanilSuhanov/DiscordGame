package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.MessageWithItems;
import ru.suhanov.discordgame.model.OperationTag;
import ru.suhanov.discordgame.model.map.Galaxy;
import ru.suhanov.discordgame.model.miner.ResourceType;
import ru.suhanov.discordgame.model.mods.GalaxyMod;
import ru.suhanov.discordgame.model.mods.Mod;
import ru.suhanov.discordgame.model.mods.UserMod;
import ru.suhanov.discordgame.repository.ModRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ModService {
    private final ModRepository modRepository;
    private final GalaxyService galaxyService;
    private final GameUserService gameUserService;

    public String getModsInfo() {
        List<Mod> mods = modRepository.findAll();
        return null;//TODO
    }

    public void createMod(ResourceType resourceType, int percent, OperationTag operationTag, String title) {
        Mod mod = new Mod(resourceType, percent, operationTag, title);
        modRepository.save(mod);
    }

    public void addModForGalaxy(String modTitle, String galaxyTitle) throws DataBaseException {
        Galaxy galaxy = galaxyService.findGalaxyByTitle(galaxyTitle);
        GalaxyMod galaxyMod = (GalaxyMod) findModByTitle(modTitle);
        galaxyMod.setGalaxy(galaxy);
        modRepository.save(galaxyMod);
    }

    public void addModForUser(String modTitle, String user) throws DataBaseException {
        GameUser gameUser = gameUserService.findGameUserByName(user);
        UserMod userMod = (UserMod) findModByTitle(modTitle);
        userMod.setGameUser(gameUser);
        modRepository.save(userMod);
    }

    public Mod findModByTitle(String title) throws DataBaseException {
        return modRepository.findByTitle(title)
                .orElseThrow(() -> new DataBaseException("Модификатор не найден!"));
    }
}
