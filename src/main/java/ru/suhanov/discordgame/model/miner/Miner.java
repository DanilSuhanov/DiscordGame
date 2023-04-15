package ru.suhanov.discordgame.model.miner;

import jakarta.persistence.*;
import lombok.Data;
import ru.suhanov.discordgame.model.Galaxy;
import ru.suhanov.discordgame.model.GameUser;

import java.time.LocalDateTime;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Miner {

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

    protected String work() {
        return "Ошибка определение типа!";
    }
}
