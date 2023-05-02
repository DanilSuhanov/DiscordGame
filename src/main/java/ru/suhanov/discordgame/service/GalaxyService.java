package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.Galaxy;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.repository.GalaxyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GalaxyService {

    public static final int MOVING_COSTS = 1;
    private final GalaxyRepository galaxyRepository;
    private final GameUserService gameUserService;

    @Autowired
    @Lazy
    public GalaxyService(GalaxyRepository galaxyRepository, GameUserService gameUserService) {
        this.galaxyRepository = galaxyRepository;
        this.gameUserService = gameUserService;
    }

    public void newGalaxy(String title, int size, boolean isStarter, List<String> neighbors) throws DataBaseException {
        Galaxy galaxy = new Galaxy(title, size, isStarter);

        neighbors.forEach(neighbor -> galaxyRepository.findGalaxyByTitle(neighbor)
                .ifPresent(galaxy::addNeighbor));

        if (!galaxyRepository.existsByTitle(galaxy.getTitle()))
            galaxyRepository.save(galaxy);
        else
            throw new DataBaseException("Галактика с таким названием уже существует!");
    }

    public Galaxy findStarterGalaxy() throws DataBaseException {
        Optional<Galaxy> galaxy = galaxyRepository.findGalaxiesByStarter(true);
        if (galaxy.isPresent()) {
            return galaxy.get();
        } else {
            throw new DataBaseException("Стартовая локация не найдена!");
        }
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

    public void moveTo(String galaxyTitle, Long userId) throws DataBaseException {
        Galaxy galaxy = findGalaxyByTitle(galaxyTitle);
        GameUser gameUser = gameUserService.findGameUserByDiscordId(userId);

        if (galaxy.getNeighbors().contains(gameUser.getLocation())) {
            if (gameUser.getOil() >= GalaxyService.MOVING_COSTS) {
                gameUser.setLocation(galaxy);
                gameUser.setOil(gameUser.getOil() - GalaxyService.MOVING_COSTS);
                gameUserService.save(gameUser);
            } else {
                throw new DataBaseException("Недостаточно топлива!");
            }
        } else {
            throw new DataBaseException("Галактика " + gameUser.getLocation().getTitle()
                    + " не имеет переход в галактику " + galaxy.getTitle());
        }
    }

    public String getString(Long userId) throws DataBaseException {
        return gameUserService.findGameUserByDiscordId(userId).getLocation().toString();
    }
}
