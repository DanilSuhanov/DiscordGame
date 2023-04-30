package ru.suhanov.discordgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.suhanov.discordgame.model.union.Faction;

@Repository
public interface FactionRepository extends JpaRepository<Faction, Long> {
    boolean existsFactionByTitle(String title);
}
