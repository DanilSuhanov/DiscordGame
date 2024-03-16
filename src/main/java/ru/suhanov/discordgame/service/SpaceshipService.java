package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.exception.StepException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.MessageWithItems;
import ru.suhanov.discordgame.model.OperationTag;
import ru.suhanov.discordgame.model.military.FleetType;
import ru.suhanov.discordgame.model.military.Spaceship;
import ru.suhanov.discordgame.model.mods.Mod;
import ru.suhanov.discordgame.repository.SpaceshipRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static ru.suhanov.discordgame.Util.getPoint;

@Service
@Transactional
@RequiredArgsConstructor
public class SpaceshipService {
    private final SpaceshipRepository spaceshipRepository;
    private final GameUserService gameUserService;
    private final StepService stepService;

    private void newSpaceship(Spaceship spaceship) throws DataBaseException {
        if (!spaceshipRepository.existsSpaceshipByTitle(spaceship.getTitle()))
            spaceshipRepository.save(spaceship);
        else throw new DataBaseException("Корабль с таким именем уже существует!");
    }

    public void createSpaceship(long userId, String title, FleetType fleetType) throws DataBaseException, StepException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(userId);
        stepService.checkStep(gameUser.getName());

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

        message.setSelectMenu(
                StringSelectMenu.create("spaceshipType")
                        .addOptions(getFleetCreatingOptions())
                        .build());

        buttons.add(Button.primary("getTypeInfo", "Информация о типах кораблей"));
        for (Spaceship spaceship : gameUser.getSpaceships()) {
            buttons.add(Button.primary("spaceshipInfo:" + spaceship.getTitle(), spaceship.getTitle()));
        }

        message.setButtons(buttons);
        message.setMessage(gameUser.getFleetInfo());
        return message;
    }

    public List<SelectOption> getFleetCreatingOptions() {
        List<SelectOption> selectOptions = new ArrayList<>();
        Arrays.stream(FleetType.values()).forEach(type ->
                selectOptions.add(SelectOption.of("Создать " + type.getTitle(),
                        "CREATE_FLEET_" + type.name())));
        return selectOptions;
    }

    public String getShipInfo(String title) throws DataBaseException {
        return findSpaceShipByTitle(title).toString();
    }

    public Spaceship findSpaceShipByTitle(String title) throws DataBaseException {
        return spaceshipRepository.findSpaceshipByTitle(title)
                .orElseThrow(() -> new DataBaseException("Корабль не найден!"));
    }

    public String getTypesInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(FleetType.values()).forEach(type -> stringBuilder.append(type.getTitle()).append(":")
                .append(getPoint("MAX HP", String.valueOf(type.getHp())))
                .append(getPoint("Ресурс для создания", type.getResourceTypeToCreate().name()))
                .append(getPoint("Стоимость создания", String.valueOf(type.getCostToCreate())))
                .append(getPoint("Ресурс для ремонта", type.getResourceTypeToService().name()))
                .append(getPoint("Стоимость ремонта - ", String.valueOf(type.getServiceCost())))
                .append("\n--------------------------------------\n"));
        return stringBuilder.toString();
    }

    public void serviceAllFleet(long userID) throws DataBaseException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(userID);
        gameUser.getSpaceships().forEach(ship -> ship.service(gameUser.getMods()
                .stream().filter(mod -> mod.getTag().equals(OperationTag.SERVICE_FLEET)).toList()));
    }
}
