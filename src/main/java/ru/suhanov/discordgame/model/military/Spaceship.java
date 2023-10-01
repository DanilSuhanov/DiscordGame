package ru.suhanov.discordgame.model.military;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.miner.ResourceType;
import ru.suhanov.discordgame.model.mods.Mod;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Spaceship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private FleetType fleetType;

    private int condition = 100;

    //Cost
    private ResourceType resourceType;
    private int cost;

    //Service
    private int serviceCost;
    private ResourceType serviceCostType;

    @ManyToOne
    private GameUser owner;

    public boolean service(List<Mod> mods) {
        return owner.addResource(serviceCost, serviceCostType, mods);
    }
}