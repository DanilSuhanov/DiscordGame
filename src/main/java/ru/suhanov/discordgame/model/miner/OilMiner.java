package ru.suhanov.discordgame.model.miner;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.suhanov.discordgame.Util;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.map.GalaxyMod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class OilMiner extends Miner{
    public static final int OUTPUT_MIN = 2;
    public static final int OUTPUT_MAX = 5;
    public static final int COST = 10;
    public OilMiner() {
        type = ResourceType.OIL;
        lastWorkTime = LocalDateTime.now().minusHours(1);
    }

    @Override
    public boolean pay(GameUser gameUser) {
        if (gameUser.getMetal() >= COST) {
            gameUser.setMetal(gameUser.getMetal() - COST);
            return true;
        }
        return false;
    }

    @Override
    protected String work(List<GalaxyMod> galaxyMods) {
        int result = Util.getRandomFromTo(OUTPUT_MIN, OUTPUT_MAX);
        owner.addResource(result, ResourceType.OIL, galaxyMods.stream()
                .filter(mod -> mod.getType().equals(ResourceType.All) || mod.getType().equals(ResourceType.OIL))
                .toList());
        return "Топливный майнер " + title + " выполнил работу. В склад было добавлено " + result + " топлива.";
    }
}
