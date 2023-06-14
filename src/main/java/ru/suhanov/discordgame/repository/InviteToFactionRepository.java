package ru.suhanov.discordgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.suhanov.discordgame.model.invite.InviteToFaction;

import java.util.List;
import java.util.Optional;

@Repository
public interface InviteToFactionRepository extends JpaRepository<InviteToFaction, Long> {
    List<InviteToFaction> findInviteToFactionsByTo_DiscordId(long id);
    Optional<InviteToFaction> findInviteToFactionByFrom_Faction_Title(String title);
}
