package ru.suhanov.discordgame.model.mods;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.suhanov.discordgame.model.GameUser;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class UserMod extends Mod {
    @ManyToOne
    private GameUser gameUser;
}
