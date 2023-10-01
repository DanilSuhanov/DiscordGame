package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.MessageWithItems;
import ru.suhanov.discordgame.model.OperationTag;
import ru.suhanov.discordgame.model.military.FleetType;
import ru.suhanov.discordgame.model.military.Spaceship;
import ru.suhanov.discordgame.model.mods.Mod;
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

    public void createSpaceship(long userId, String title, FleetType fleetType) throws DataBaseException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(userId);

        Spaceship spaceship = new Spaceship();
        spaceship.setFleetType(fleetType);

        //Create
        spaceship.setResourceType(fleetType.getResourceTypeToCreate());
        spaceship.setCost(fleetType.getCostToCreate());

        //Service
        spaceship.setServiceCostType(fleetType.getResourceTypeToService());
        spaceship.setServiceCost(fleetType.getServiceCost());

        spaceship.setOwner(gameUser);
        spaceship.setTitle(title);

        List<Mod> createFleetMods = gameUser.getMods().stream()
                .filter(mod -> mod.getTag().equals(OperationTag.FLEET_CREATING)).toList();

        if (gameUser.addResource(spaceship.getCost() * -1, spaceship.getResourceType(),
                createFleetMods)) {
            newSpaceship(spaceship);
            gameUserService.save(gameUser);
        } else {
            throw new DataBaseException("Недостаточно ресурсов для создания корабля!");
        }
    }

    public MessageWithItems getFleetInfo(long userId) throws DataBaseException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(userId);
        List<Button> buttons = new LinkedList<>();
        MessageWithItems message = new MessageWithItems();

        message.setSelectMenu(StringSelectMenu.create("spaceshipType")
                .addOption("Создать маленький корабль", "CREATE_FLEET_SMALL")
                .addOption("Создать средний корабль", "CREATE_FLEET_MEDIUM")
                .addOption("Создать большой корабль", "CREATE_FLEET_LARGE")
                .build());

        buttons.add(Button.primary("createSpaceship", "Создать корабль"));
        for (Spaceship spaceship : gameUser.getSpaceships()) {
            buttons.add(Button.primary("spaceshipInfo:" + spaceship.getTitle(), spaceship.getTitle()));
        }

        message.setButtons(buttons);
        message.setMessage(gameUser.getFleetInfo());
        return message;
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
