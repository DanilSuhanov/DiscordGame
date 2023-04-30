package ru.suhanov.discordgame.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.suhanov.discordgame.model.military.Spaceship;
import ru.suhanov.discordgame.model.miner.Miner;
import ru.suhanov.discordgame.model.miner.ResourceType;
import ru.suhanov.discordgame.model.union.Faction;

import java.util.List;
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

    private int oil;

    private int metal;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Miner> miners;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Spaceship> spaceships;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Galaxy location;


    //Unions
    @ManyToOne(fetch = FetchType.EAGER)
    private Faction faction;

    //OwnedFaction
    @OneToOne(mappedBy = "leader")
    private Faction ownedFaction;


    public void addResource(int amount, ResourceType type) {
        switch (type) {
            case METAL -> {
                metal += amount;
            }
            case OIL -> oil += amount;
        }
    }

    public GameUser(String name, Long discordId, Long money, Galaxy galaxy, int oil, int metal) {
        this.name = name;
        this.discordId = discordId;
        this.money = money;
        this.location = galaxy;
        this.oil = oil;
        this.metal = metal;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Имя - ").append(name);
        if (faction != null) {
            stringBuilder.append("\nФракция - ").append(faction.getTitle());
        } else if (ownedFaction != null) {
            stringBuilder.append("\nФракция - ").append(ownedFaction.getTitle());
        }
        stringBuilder.append("\nДеньги - ").append(money)
                .append("\nТопливо - ").append(oil)
                .append("\nМетал - ").append(metal)
                .append("\nТекущая галактика - ").append(location.getTitle());
        if (miners.size() > 0) {
            stringBuilder.append("\n\nМайнеры: ");
            for (Miner miner : miners) {
                stringBuilder.append("\n").append(miner.getTitle()).append(" - ").append(miner.getType().toString());
            }
        }
        if (spaceships.size() > 0) {
            stringBuilder.append("\n\nКорабли: ");
            stringBuilder.append("\nСреднее состояние кораблей - ")
                    .append(spaceships.stream().map(Spaceship::getCondition).reduce(Integer::sum).get() / spaceships.size());
            for (Spaceship spaceship : spaceships) {
                stringBuilder.append("\n").append(spaceship.getTitle()).append(": состояние - ").append(spaceship.getCondition());
            }
        }
        return stringBuilder.toString();
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
