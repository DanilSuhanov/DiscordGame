package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.Galaxy;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.repository.GameUserRepository;

@Service
@Transactional
public class GameUserService {

    public static final long START_MONEY = 0L;
    public static final int START_OIL = 30;
    public static final int START_METAL = 60;

    private final GameUserRepository gameUserRepository;
    private final GalaxyService galaxyService;

    @Autowired
    public GameUserService(GameUserRepository gameUserRepository, GalaxyService galaxyService) {
        this.gameUserRepository = gameUserRepository;
        this.galaxyService = galaxyService;
    }

    public void newGameUser(String name, Long id) throws DataBaseException {
        Galaxy galaxy = galaxyService.findStarterGalaxy();
        GameUser gameUser = new GameUser(name, id, START_MONEY, galaxy, START_OIL, START_METAL);

        if (!gameUserRepository.existsGameUserByNameOrDiscordId(gameUser.getName(), gameUser.getDiscordId()))
            gameUserRepository.save(gameUser);
        else
            throw new DataBaseException("Создаваемый пользователь уже существует!");
    }

    public GameUser findGameUserByDiscordId(Long discordId) throws DataBaseException {
        GameUser gameUser = gameUserRepository.findGameUserByDiscordId(discordId).orElse(null);
        if (gameUser != null)
            return gameUser;
        else
            throw new DataBaseException("Пользователь не найден!");
    }

    public String getString(Long userId) throws DataBaseException {
        return findGameUserByDiscordId(userId).toString();
    }

    public void save(GameUser gameUser) {
        gameUserRepository.save(gameUser);
    }
}
