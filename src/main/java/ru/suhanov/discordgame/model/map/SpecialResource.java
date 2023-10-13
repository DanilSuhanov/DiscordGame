package ru.suhanov.discordgame.model.map;

import lombok.Getter;

@Getter
public enum SpecialResource {
    STEEL("Сталь");

    SpecialResource(String title) {
        this.title = title;
    }

    private final String title;
}
