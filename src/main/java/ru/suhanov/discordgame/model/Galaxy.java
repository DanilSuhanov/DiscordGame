package ru.suhanov.discordgame.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public Galaxy(String title, int size, boolean starter) {
        this.title = title;
        this.size = size;
        this.starter = starter;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GameUser> gameUsers = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Galaxy> neighbors = new ArrayList<>();

    public String moveTo(GameUser gameUser) {
        if (neighbors.contains(gameUser.getLocation())) {
            gameUser.setLocation(this);
            return gameUser.getName() + " перешёл в галактику " + title;
        } else {
            return "Галактика " + gameUser.getLocation().getTitle()
                    + " не имеет переход в галактику " + title;
        }
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