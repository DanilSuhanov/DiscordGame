package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
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

import java.util.ArrayList;
import java.util.List;

import static ru.suhanov.discordgame.Util.listToStringList;

@Service
@Transactional
@RequiredArgsConstructor
public class ModService {
    private final ModRepository modRepository;
    private final GalaxyService galaxyService;
    private final GameUserService gameUserService;

    public MessageWithItems getModsInfo() {
        MessageWithItems messageWithItems = new MessageWithItems();
        List<Mod> mods = modRepository.findAll();

        messageWithItems.setMessage(listToStringList("Модификаторы", mods).toString());
        messageWithItems.addButton(Button.primary("createModifier",
                "Создать новый модификатор"));
        messageWithItems.addButton(Button.primary("addModifierToUser",
                "Добавить модификатор для пользователя"));
        messageWithItems.addButton(Button.primary("addModifierToGalaxy",
                "Добавить модификартор для галактики"));

        List<Button> modButtons = new ArrayList<>();
        for (Mod mod : mods) {
            modButtons.add(Button.primary("MOD_INFO" + mod.getTitle(), mod.getTitle()));
        }
        messageWithItems.addButtons(modButtons);

        return messageWithItems;
    }

    public void createMod(ResourceType resourceType, int percent, OperationTag operationTag, String title) {
        Mod mod = new Mod(resourceType, percent, operationTag, title);
        modRepository.save(mod);
    }

    public void addModForGalaxy(String modTitle, String galaxyTitle) throws DataBaseException {
        Galaxy galaxy = galaxyService.findGalaxyByTitle(galaxyTitle);
        GalaxyMod galaxyMod = new GalaxyMod(findModByTitle(modTitle));
        galaxyMod.setGalaxy(galaxy);
        modRepository.save(galaxyMod);
    }

    public void addModForUser(String modTitle, String user) throws DataBaseException {
        GameUser gameUser = gameUserService.findGameUserByName(user);
        UserMod userMod = new UserMod(findModByTitle(modTitle));
        userMod.setGameUser(gameUser);
        modRepository.save(userMod);
    }

    public Mod findModByTitle(String title) throws DataBaseException {
        return modRepository.findByTitle(title)
                .orElseThrow(() -> new DataBaseException("Модификатор не найден!"));
    }
}
