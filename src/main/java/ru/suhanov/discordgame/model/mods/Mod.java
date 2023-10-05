package ru.suhanov.discordgame.model.mods;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.suhanov.discordgame.model.OperationTag;
import ru.suhanov.discordgame.model.miner.ResourceType;

import java.util.Objects;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
public class Mod {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    private ResourceType type;

    private int percent;

    private OperationTag tag;

    public Mod(ResourceType resourceType, int percent, OperationTag operationTag, String title) {
        this.title = title;
        this.type = resourceType;
        this.percent = percent;
        this.tag = operationTag;
    }

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
