package ru.suhanov.discordgame.model.miner;

import jakarta.persistence.*;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import ru.suhanov.discordgame.model.map.Galaxy;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.mods.GalaxyMod;
import ru.suhanov.discordgame.model.mods.Mod;

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
    private int reloadTime = 1;

    public boolean pay(GameUser gameUser) {
        return true;
    }

    @ManyToOne
    protected GameUser owner;

    @ManyToOne
    protected Galaxy location;

    protected String work() {
        return "Ошибка определение типа!";
    }

    public String start() {
        if (!readyToWork())
            return "Майнер " + title + " ещё не готов к работе!";
        String res = work();
        lastWorkTime = LocalDateTime.now();
        return res;
    }

    public boolean readyToWork() {
        return lastWorkTime.plusHours(reloadTime).isBefore(LocalDateTime.now());
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

    public static List<Mod> getModFromMinersMods(List<GalaxyMod> galaxyMods, ResourceType type) {
        return galaxyMods.stream().filter(mod -> mod.getType()
                .equals(ResourceType.ALL) || mod.getType().equals(type))
                .map(m -> (Mod) m).toList();
    }
}
