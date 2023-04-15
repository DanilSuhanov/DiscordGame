package ru.suhanov.discordgame.model.miner;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.suhanov.discordgame.model.GameUser;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class OilMiner extends Miner{

    public static final int OUTPUT = 5;
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
    protected String work() {
        owner.setOil(owner.getOil() + OUTPUT);
        return "Топливный майнер " + title + " выполнил работу!";
    }
}
