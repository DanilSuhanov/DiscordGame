package ru.suhanov.discordgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.suhanov.discordgame.model.GameUser;

import java.util.Optional;

@Repository
public interface GameUserRepository extends JpaRepository<GameUser, Long> {
    boolean existsGameUserByNameOrDiscordId(String name, Long discordId);
    Optional<GameUser> findGameUserByDiscordId(Long discordId);
}
