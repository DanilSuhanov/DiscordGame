package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.exception.UserNotFoundException;
import ru.suhanov.discordgame.model.Galaxy;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.repository.GameUserRepository;

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

    public void newGameUser(String name, Long id) throws DataBaseException {
        Galaxy galaxy = galaxyService.findStarterGalaxy();
        GameUser gameUser = new GameUser(name, id, START_MONEY, galaxy, START_OIL, START_METAL);

        if (!gameUserRepository.existsGameUserByNameOrDiscordId(gameUser.getName(), gameUser.getDiscordId()))
            gameUserRepository.save(gameUser);
        else
            throw new DataBaseException("Создаваемый пользователь уже существует!");
    }

    public GameUser findGameUserByDiscordId(Long discordId) throws DataBaseException {
        return gameUserRepository.findGameUserByDiscordId(discordId)
                .orElseThrow(UserNotFoundException::new);
    }

    public GameUser findGameUserByName(String name) throws DataBaseException {
        return gameUserRepository.findGameUserByName(name)
                .orElseThrow(UserNotFoundException::new);
    }

    public String getString(Long userId) throws DataBaseException {
        return findGameUserByDiscordId(userId).toString();
    }

    public void save(GameUser gameUser) {
        gameUserRepository.save(gameUser);
    }
}
