package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.Galaxy;
import ru.suhanov.discordgame.repository.GalaxyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GalaxyService {
    private final GalaxyRepository galaxyRepository;

    @Autowired
    public GalaxyService(GalaxyRepository galaxyRepository) {
        this.galaxyRepository = galaxyRepository;
    }

    public void newGalaxy(Galaxy galaxy) throws DataBaseException {
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
}
