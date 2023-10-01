package ru.suhanov.discordgame.model.military;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.suhanov.discordgame.model.miner.ResourceType;

@Getter
@AllArgsConstructor
public enum FleetType {
    SMALL(ResourceType.METAL, 10, ResourceType.METAL, 5,30),
    MEDIUM(ResourceType.METAL, 30, ResourceType.METAL, 15, 100),
    LARGE(ResourceType.METAL, 75, ResourceType.METAL, 30, 300);

    private final ResourceType resourceTypeToCreate;
    private final int costToCreate;

    private final ResourceType resourceTypeToService;
    private final int serviceCost;

    private final int hp;
}