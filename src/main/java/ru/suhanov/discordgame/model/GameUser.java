package ru.suhanov.discordgame.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.suhanov.discordgame.model.invite.InviteToFaction;
import ru.suhanov.discordgame.model.map.Galaxy;
import ru.suhanov.discordgame.model.military.Spaceship;
import ru.suhanov.discordgame.model.miner.Miner;
import ru.suhanov.discordgame.model.miner.ResourceType;
import ru.suhanov.discordgame.model.mods.Mod;
import ru.suhanov.discordgame.model.mods.UserMod;
import ru.suhanov.discordgame.model.union.Faction;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
public class GameUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private long discordId;

    private long money;

    private int oil;

    private int metal;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Miner> miners;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Spaceship> spaceships;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Galaxy location;

    //Invites
    @OneToMany(mappedBy = "from", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<InviteToFaction> outgoing;

    @OneToMany(mappedBy = "to", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<InviteToFaction> incoming;


    //Unions
    @ManyToOne(fetch = FetchType.EAGER)
    private Faction faction;

    //OwnedFaction
    @OneToOne(mappedBy = "leader")
    private Faction ownedFaction;

    //Mods
    @OneToMany
    private List<UserMod> mods = new LinkedList<>();
    public List<Mod> getMods() {
        return mods.stream().map(mod -> (Mod) mod).toList();
    }


    public boolean addResource(int amount, ResourceType type, List<Mod> mods) {
        int res = amount;
        for (Mod galaxyMod : mods) {
            res = galaxyMod.result(res);
        }
        switch (type) {
            case METAL -> {
                if (metal + res > 0) {
                    metal += res;
                    return true;
                }
            }
            case OIL -> {
                if (oil + res > 0) {
                    oil += res;
                    return true;
                }
            }
        }
        return false;
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
        stringBuilder.append("\nТекущая галактика - ").append(location.getTitle())
                .append("\nДеньги - ").append(money)
                .append("\nТопливо - ").append(oil)
                .append("\nМетал - ").append(metal);
        return stringBuilder.toString();
    }

    public String getMinersInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        if (miners.size() > 0) {
            stringBuilder.append("Майнеры: ");
            for (Miner miner : miners) {
                stringBuilder.append("\n").append(miner.getTitle())
                        .append(" - ").append(miner.getType().toString())
                        .append(" - ");
                if (miner.readyToWork()) {
                    stringBuilder.append("Готов к работе!");
                } else {
                    stringBuilder.append("Не готов к работе, осталось - ")
                            .append(miner.getReloadTime() * 60L - Duration.between(miner.getLastWorkTime(), LocalDateTime.now()).getSeconds()/60)
                            .append(" минут.");
                }
            }
        } else {
            stringBuilder.append("Список майнеров пуст!");
        }
        return stringBuilder.toString();
    }

    public String getFleetInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!spaceships.isEmpty()) {
            stringBuilder.append("Корабли: ");
            stringBuilder.append("\nСреднее состояние кораблей - ")
                    .append(spaceships.stream().map(Spaceship::getCondition).reduce(Integer::sum).get() / spaceships.size());
            for (Spaceship spaceship : spaceships) {
                stringBuilder.append("\n").append(spaceship.getTitle())
                        .append(": тип - ").append(spaceship.getFleetType().getTitle())
                        .append(", состояние - ").append(spaceship.getCondition());
            }
        } else {
            stringBuilder.append("Список кораблей пуст!");
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameUser gameUser = (GameUser) o;
        return id == gameUser.id && discordId == gameUser.discordId && name.equals(gameUser.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, discordId);
    }
}
