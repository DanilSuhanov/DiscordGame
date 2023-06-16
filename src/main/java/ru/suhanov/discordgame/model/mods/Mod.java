package ru.suhanov.discordgame.model.mods;

import jakarta.persistence.*;
import lombok.Data;
import ru.suhanov.discordgame.model.miner.ResourceType;

import java.util.Objects;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Mod {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private ResourceType type;

    private int percent;

    public int result(int resource) {
        return resource * percent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mod mod = (Mod) o;
        return id == mod.id && percent == mod.percent && type == mod.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, percent);
    }
}
