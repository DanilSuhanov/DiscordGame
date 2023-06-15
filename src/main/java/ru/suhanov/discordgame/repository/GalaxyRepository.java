package ru.suhanov.discordgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.suhanov.discordgame.model.map.Galaxy;

import java.util.Optional;

@Repository
public interface GalaxyRepository extends JpaRepository<Galaxy, Long> {
    boolean existsByTitle(String title);
    Optional<Galaxy> findGalaxyByTitle(String title);
}
