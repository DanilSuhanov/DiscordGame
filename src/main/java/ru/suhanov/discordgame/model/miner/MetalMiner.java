package ru.suhanov.discordgame.model.miner;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.suhanov.discordgame.Util;
import ru.suhanov.discordgame.model.GameUser;

import java.time.LocalDateTime;
import java.util.Random;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class MetalMiner extends Miner {
    public static final int OUTPUT_MIN = 5;
    public static final int OUTPUT_MAX = 10;
    public static final int WORK_COST = 3;
    public static final int COST = 20;
    public MetalMiner() {
        type = ResourceType.METAL;
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
    protected String work() {
        if (owner.getOil() >= WORK_COST) {
            owner.setOil(owner.getOil() - WORK_COST);
            int result = Util.getRandomFromTo(OUTPUT_MIN, OUTPUT_MAX);
            owner.setMetal(owner.getMetal() + result);
            return "Метал майнер " + title + " выполнил работу. В склад было добавлено " + result + " метала.";
        } else {
            return "Недостаточно топлива для запуска метал майнера " + title;
        }
    }
}
