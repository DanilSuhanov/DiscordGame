package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.MessageWithButtons;
import ru.suhanov.discordgame.model.map.Galaxy;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.repository.GalaxyRepository;

import java.util.*;

@Service
@Transactional
public class GalaxyService {
    @Value("${MOVING_COSTS}")
    public int MOVING_COSTS;
    @Value("${URL.MAP}")
    public String MAP;
    private final GalaxyRepository galaxyRepository;
    private final GameUserService gameUserService;

    @Autowired
    @Lazy
    public GalaxyService(GalaxyRepository galaxyRepository, GameUserService gameUserService) {
        this.galaxyRepository = galaxyRepository;
        this.gameUserService = gameUserService;
    }

    public void newGalaxy(String title, int size, List<String> neighbors) throws DataBaseException {
        if (!galaxyRepository.existsByTitle(title)) {
            Galaxy galaxy = new Galaxy(title, size);

            for (String neigStr : neighbors) {
                Galaxy neig = galaxyRepository.findGalaxyByTitle(neigStr)
                        .orElseThrow(() -> new DataBaseException("Галактика не найдена!"));
                neig.addNeighbor(galaxy);
                galaxy.addNeighbor(neig);
            }
            galaxyRepository.save(galaxy);
        }
        else
            throw new DataBaseException("Галактика с таким названием уже существует!");
    }

    public void newGalaxy(String title, int size) throws DataBaseException {
        Galaxy galaxy = new Galaxy(title, size);

        if (!galaxyRepository.existsByTitle(galaxy.getTitle()))
            galaxyRepository.save(galaxy);
        else
            throw new DataBaseException("Галактика с таким названием уже существует!");
    }

    public Galaxy getRandomGalaxy() {
        List<Galaxy> galaxies = galaxyRepository.findAll();
        int random = new Random().nextInt(0, galaxies.size());
        return galaxies.get(random);
    }

    public List<Galaxy> findAllGalaxyByTitle(List<String> titles) {
        List<Galaxy> galaxies = new ArrayList<>();
        titles.forEach(title -> galaxyRepository.findGalaxyByTitle(title).ifPresent(galaxies::add));
        return galaxies;
    }

    public Galaxy findGalaxyByTitle(String title) throws DataBaseException {
        Optional<Galaxy> galaxy = galaxyRepository.findGalaxyByTitle(title);
        if (galaxy.isPresent()) {
            return galaxy.get();
        } else {
            throw new DataBaseException("Такой галактики не существует!");
        }
    }

    public MessageWithButtons getMap() {
        List<Galaxy> galaxies = galaxyRepository.findAll();
        StringBuilder stringBuilder = new StringBuilder("Список галактик:\n");
        List<Button> buttons = new LinkedList<>();
        for (Galaxy galaxy : galaxies) {
            stringBuilder.append(galaxy.getTitle())
                    .append("\n");
            buttons.add(Button.primary("galaxyInfo:" + galaxy.getTitle(), galaxy.getTitle()));
        }
        return new MessageWithButtons(stringBuilder.toString(), buttons, MAP);
    }

    public void moveTo(String galaxyTitle, Long userId) throws DataBaseException {
        Galaxy galaxy = findGalaxyByTitle(galaxyTitle);
        GameUser gameUser = gameUserService.findGameUserByDiscordId(userId);

        if (galaxy.getNeighbors().contains(gameUser.getLocation())) {
            if (gameUser.getOil() >= MOVING_COSTS) {
                gameUser.setLocation(galaxy);
                gameUser.setOil(gameUser.getOil() - MOVING_COSTS);
                gameUserService.save(gameUser);
            } else {
                throw new DataBaseException("Недостаточно топлива!");
            }
        } else {
            throw new DataBaseException("Галактика " + gameUser.getLocation().getTitle()
                    + " не имеет переход в галактику " + galaxy.getTitle());
        }
    }

    public MessageWithButtons galaxyToString(String title, long id) throws DataBaseException {
        Galaxy galaxy = galaxyRepository.findGalaxyByTitle(title)
                .orElseThrow(() -> new DataBaseException("Галактика не найдена!"));
        GameUser gameUser = gameUserService.findGameUserByDiscordId(id);
        MessageWithButtons message;
        if (gameUser.getLocation().getNeighbors().contains(galaxy)) {
            message = new MessageWithButtons(galaxy.toString(),
                    List.of(Button.primary("moveTo:" + galaxy.getTitle(), "Move")));
        } else {
            message = new MessageWithButtons(galaxy.toString());
        }
        return message;
    }
}
