package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.exception.UserNotFoundException;
import ru.suhanov.discordgame.model.MessageWithButtons;
import ru.suhanov.discordgame.model.map.Galaxy;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.miner.Miner;
import ru.suhanov.discordgame.repository.GameUserRepository;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class GameUserService {
    @Value("${START_MONEY}")
    private long START_MONEY;
    @Value("${START_OIL}")
    private int START_OIL;
    @Value("${START_METAL}")
    private int START_METAL;

    private final GameUserRepository gameUserRepository;
    private final GalaxyService galaxyService;

    @Autowired
    public GameUserService(GameUserRepository gameUserRepository, GalaxyService galaxyService) {
        this.gameUserRepository = gameUserRepository;
        this.galaxyService = galaxyService;
    }

    public void newGameUser(String name, long id) throws DataBaseException {
        Galaxy galaxy = galaxyService.getRandomGalaxy();
        GameUser gameUser = new GameUser(name, id, START_MONEY, galaxy, START_OIL, START_METAL);

        if (!gameUserRepository.existsGameUserByNameOrDiscordId(gameUser.getName(), gameUser.getDiscordId()))
            gameUserRepository.save(gameUser);
        else
            throw new DataBaseException("Создаваемый пользователь уже существует!");
    }

    public GameUser findGameUserByDiscordId(long discordId) throws DataBaseException {
        return gameUserRepository.findGameUserByDiscordId(discordId)
                .orElseThrow(UserNotFoundException::new);
    }

    public GameUser findGameUserByName(String name) throws DataBaseException {
        return gameUserRepository.findGameUserByName(name)
                .orElseThrow(UserNotFoundException::new);
    }

    public String getString(long userId) throws DataBaseException {
        return findGameUserByDiscordId(userId).toString();
    }

    public MessageWithButtons getMinersInfo(long userId) throws DataBaseException {
        GameUser gameUser = findGameUserByDiscordId(userId);
        String info = gameUser.getMinersInfo();
        List<Button> buttons = new LinkedList<>();
        buttons.add(Button.primary("workAllMiners", "Запустить все майнеры"));
        for (Miner miner : gameUser.getMiners()) {
            buttons.add(Button.primary("workMiner:" + miner.getTitle(), "Запустить " + miner.getTitle()));
        }
        return new MessageWithButtons(info, buttons);
    }

    public void save(GameUser gameUser) {
        gameUserRepository.save(gameUser);
    }
}
