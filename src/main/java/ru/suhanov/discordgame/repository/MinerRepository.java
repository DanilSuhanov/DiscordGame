package ru.suhanov.discordgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.suhanov.discordgame.model.miner.Miner;

@Repository
public interface MinerRepository extends JpaRepository<Miner, Long> {
    boolean existsMinerByTitle(String title);
}
