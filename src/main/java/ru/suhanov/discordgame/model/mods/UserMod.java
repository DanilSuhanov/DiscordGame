package ru.suhanov.discordgame.model.mods;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.OperationTag;
import ru.suhanov.discordgame.model.miner.ResourceType;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
public class UserMod extends Mod {
    @ManyToOne
    private GameUser gameUser;

    public UserMod(ResourceType resourceType, int percent, OperationTag operationTag, String title) {
        super(resourceType, percent, operationTag, title);
    }
}
