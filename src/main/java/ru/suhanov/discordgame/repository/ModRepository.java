package ru.suhanov.discordgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.suhanov.discordgame.model.mods.Mod;

import java.util.Optional;

@Repository
public interface ModRepository extends JpaRepository<Mod, Long> {
    Optional<Mod> findByTitle(String title);
}
