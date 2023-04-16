package ru.suhanov.discordgame.model.military;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.suhanov.discordgame.model.GameUser;

@Data
@NoArgsConstructor
@Entity
public class Spaceship {
    public static final int COST = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private int condition = 100;

    @ManyToOne
    private GameUser owner;

    public Spaceship(String title, GameUser gameUser) {
        this.title = title;
        this.owner = gameUser;
    }

    public boolean buy(GameUser gameUser) {
        if (gameUser.getMetal() >= COST) {
            gameUser.setMetal(gameUser.getMetal() - COST);
            owner = gameUser;
            return true;
        }
        return false;
    }
}