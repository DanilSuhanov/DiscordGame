package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.unions.ChannelUnion;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.MessageWithButtons;
import ru.suhanov.discordgame.model.military.Spaceship;
import ru.suhanov.discordgame.repository.SpaceshipRepository;

import java.util.LinkedList;
import java.util.List;

@Service
@EnableScheduling
@Transactional
public class SpaceshipService {
    private final SpaceshipRepository spaceshipRepository;
    private final GameUserService gameUserService;

    @Autowired
    public SpaceshipService(SpaceshipRepository spaceshipRepository, GameUserService gameUserService) {
        this.spaceshipRepository = spaceshipRepository;
        this.gameUserService = gameUserService;
    }

    private void newSpaceship(Spaceship spaceship) throws DataBaseException {
        if (!spaceshipRepository.existsSpaceshipByTitle(spaceship.getTitle()))
            spaceshipRepository.save(spaceship);
        else throw new DataBaseException("Корабль с таким именем уже существует!");
    }

    public void createSpaceship(long userId, String title) throws DataBaseException {
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

    public MessageWithButtons getFleetInfo(long userId) throws DataBaseException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(userId);
        List<Button> buttons = new LinkedList<>();
        buttons.add(Button.primary("createSpaceship", "Создать корабль"));
        for (Spaceship spaceship : gameUser.getSpaceships()) {
            buttons.add(Button.primary("spaceshipInfo:" + spaceship.getTitle(), spaceship.getTitle()));
        }
        return new MessageWithButtons(gameUser.getFleetInfo(), buttons);
    }

    @Scheduled(fixedDelay = 60000L)
    public void serviceFleet() {
        spaceshipRepository.findAll().forEach(ship -> {
            if (!ship.service(ship.getOwner().getMods())) {
                spaceshipRepository.delete(ship);
            }
        });
    }
}
