package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.repository.GameUserRepository;

@Service
@Transactional
public class GameUserService {

    private final GameUserRepository gameUserRepository;

    @Autowired
    public GameUserService(GameUserRepository gameUserRepository) {
        this.gameUserRepository = gameUserRepository;
    }

    public void newGameUser(GameUser gameUser) throws DataBaseException {
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
}
