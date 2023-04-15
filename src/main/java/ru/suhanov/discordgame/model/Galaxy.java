package ru.suhanov.discordgame.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.suhanov.discordgame.model.miner.Miner;
import ru.suhanov.discordgame.service.GalaxyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
public class Galaxy {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private int size;

    private boolean starter;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GameUser> gameUsers = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Galaxy> neighbors = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Miner> miners = new ArrayList<>();

    public Galaxy(String title, int size, boolean starter) {
        this.title = title;
        this.size = size;
        this.starter = starter;
    }

    public void addNeighbors(List<Galaxy> galaxies) {
        if (galaxies.size() > 0)
            neighbors.addAll(galaxies);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Galaxy galaxy = (Galaxy) o;

        if (!Objects.equals(id, galaxy.id)) return false;
        return Objects.equals(title, galaxy.title);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
