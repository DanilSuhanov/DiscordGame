package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.military.Spaceship;
import ru.suhanov.discordgame.repository.SpaceshipRepository;

@Service
@Transactional
public class SpaceshipService {
    private final SpaceshipRepository spaceshipRepository;
    private final GameUserService gameUserService;

    @Autowired
    public SpaceshipService(SpaceshipRepository spaceshipRepository, GameUserService gameUserService) {
        this.spaceshipRepository = spaceshipRepository;
        this.gameUserService = gameUserService;
    }

    public void newSpaceship(Spaceship spaceship) throws DataBaseException {
        if (!spaceshipRepository.existsSpaceshipByTitle(spaceship.getTitle()))
            spaceshipRepository.save(spaceship);
        else
            throw new DataBaseException("Корабль с таким именем уже существует!");
    }

    public void createSpaceship(Long userId, String title) throws DataBaseException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(userId);
        Spaceship spaceship = new Spaceship();
        if (spaceship.buy(gameUser)) {
            spaceship.setTitle(title);
            newSpaceship(spaceship);
            gameUserService.save(gameUser);
        } else {
            throw new DataBaseException("Недостаточно ресурсов для создания корабля!");
        }
    }
}
