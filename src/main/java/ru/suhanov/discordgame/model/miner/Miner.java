package ru.suhanov.discordgame.model.miner;

import jakarta.persistence.*;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import ru.suhanov.discordgame.model.map.Galaxy;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.map.GalaxyMod;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Miner implements Comparable<Miner> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    protected ResourceType type;

    protected String title = "NotSet";

    protected LocalDateTime lastWorkTime;

    public boolean pay(GameUser gameUser) {
        return true;
    }

    @ManyToOne
    protected GameUser owner;

    @ManyToOne
    protected Galaxy location;

    protected String work(List<GalaxyMod> galaxyMods) {
        return "Ошибка определение типа!";
    }

    public String start(List<GalaxyMod> galaxyMods) {
        if (!readyToWork())
            return "Майнер " + title + " ещё не готов к работе!";
        String res = work(galaxyMods);
        lastWorkTime = LocalDateTime.now();
        return res;
    }

    protected boolean readyToWork() {
        return lastWorkTime.plusHours(1).isBefore(LocalDateTime.now());
    }

    @Override
    public int compareTo(@NotNull Miner miner2) {
        if (type == ResourceType.OIL && miner2.getType() != ResourceType.OIL) {
            return -1;
        } else if (type != ResourceType.OIL && miner2.getType() == ResourceType.OIL) {
            return 1;
        }
        return 0;
    }
}
