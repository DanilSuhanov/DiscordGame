package ru.suhanov.discordgame.model.mods;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.suhanov.discordgame.model.map.Galaxy;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class GalaxyMod extends Mod {
    @ManyToOne
    private Galaxy galaxy;
}
