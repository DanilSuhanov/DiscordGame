package ru.suhanov.discordgame.model.military;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.suhanov.discordgame.model.miner.ResourceType;

@Getter
@AllArgsConstructor
public enum FleetType {
    SMALL(ResourceType.METAL, 10, ResourceType.METAL, 5,30, "Маленький корабль"),
    MEDIUM(ResourceType.METAL, 30, ResourceType.METAL, 15, 100, "Средний корабль"),
    LARGE(ResourceType.METAL, 75, ResourceType.METAL, 30, 300, "Большой корабль");

    private final ResourceType resourceTypeToCreate;
    private final int costToCreate;

    private final ResourceType resourceTypeToService;
    private final int serviceCost;

    private final int hp;

    private final String title;
}