package ru.suhanov.discordgame.model.mods;

import jakarta.persistence.*;
import lombok.*;
import ru.suhanov.discordgame.model.OperationTag;
import ru.suhanov.discordgame.model.map.Galaxy;
import ru.suhanov.discordgame.model.miner.ResourceType;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
public class GalaxyMod extends Mod {
    @ManyToOne
    private Galaxy galaxy;

    public GalaxyMod(ResourceType resourceType, int percent, OperationTag operationTag, String title) {
        super(resourceType, percent, operationTag, title);
    }
}
