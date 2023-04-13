package ru.suhanov.discordgame.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class GameUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Long discordId;

    private Long money;

    public GameUser(String name, Long discordId, Long money) {
        this.name = name;
        this.discordId = discordId;
        this.money = money;
    }
}
