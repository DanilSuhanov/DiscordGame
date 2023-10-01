package ru.suhanov.discordgame.model.military;

import lombok.Getter;
import ru.suhanov.discordgame.model.miner.ResourceType;

@Getter
public enum FleetType {
    SMALL(ResourceType.METAL, 10, 30),
    MEDIUM(ResourceType.METAL, 30, 100),
    LARGE(ResourceType.METAL, 75, 300);

    private final ResourceType resourceType;
    private final int cost;
    private final int hp;

    FleetType(ResourceType resourceType, int cost, int hp) {
        this.resourceType = resourceType;
        this.cost = cost;
        this.hp = hp;
    }

}