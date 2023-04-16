package ru.suhanov.discordgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.suhanov.discordgame.model.military.Spaceship;

@Repository
public interface SpaceshipRepository extends JpaRepository<Spaceship, Long> {
    boolean existsSpaceshipByTitle(String title);
}
