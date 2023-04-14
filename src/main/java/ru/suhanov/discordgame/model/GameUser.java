package ru.suhanov.discordgame.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @ManyToOne(fetch = FetchType.EAGER)
    private Galaxy location;

    public GameUser(String name, Long discordId, Long money, Galaxy galaxy) {
        this.name = name;
        this.discordId = discordId;
        this.money = money;
        this.location = galaxy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameUser gameUser = (GameUser) o;

        if (!Objects.equals(id, gameUser.id)) return false;
        if (!Objects.equals(name, gameUser.name)) return false;
        return Objects.equals(discordId, gameUser.discordId);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (discordId != null ? discordId.hashCode() : 0);
        return result;
    }
}
