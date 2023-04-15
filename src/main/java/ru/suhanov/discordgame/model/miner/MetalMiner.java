package ru.suhanov.discordgame.model.miner;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.suhanov.discordgame.model.GameUser;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class MetalMiner extends Miner {
    public static final int OUTPUT = 10;
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
            owner.setMetal(owner.getMetal() + OUTPUT);
            return "Метал майнер " + title + " выполнил работу!";
        } else {
            return "Недостаточно топлива для запуска метал майнера " + title;
        }
    }
}
