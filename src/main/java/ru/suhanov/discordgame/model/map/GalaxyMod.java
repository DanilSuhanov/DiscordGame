package ru.suhanov.discordgame.model.map;

import jakarta.persistence.*;
import lombok.Data;
import ru.suhanov.discordgame.model.miner.ResourceType;

@Data
@Entity
public class GalaxyMod {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Galaxy galaxy;

    private ResourceType type;

    private int percent;

    public int result(int resource) {
        return resource * percent;
    }
}
