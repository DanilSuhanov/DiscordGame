package ru.suhanov.discordgame.model.map;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.suhanov.discordgame.Util;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.miner.Miner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
public class Galaxy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private int size;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GameUser> gameUsers = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "galaxy_neighbors",
            joinColumns = @JoinColumn(name = "galaxy_id"),
            inverseJoinColumns = @JoinColumn(name = "neighbor_id"))
    private List<Galaxy> neighbors = new ArrayList<>();

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Miner> miners = new ArrayList<>();

    @OneToMany(mappedBy = "galaxy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GalaxyMod> galaxyMods = new ArrayList<>();

    public Galaxy(String title, int size) {
        this.title = title;
        this.size = size;
    }

    public void addNeighbor(Galaxy galaxy) {
        neighbors.add(galaxy);
    }

    @Override
    @Transactional
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Информаци о галактике:")
                .append("\nНазвание - ").append(title)
                .append("\nРазмер - ").append(size);
        if (getGameUsers().size() > 0) {
            stringBuilder.append("\n\nИгроки:");
            for (GameUser gameUser : getGameUsers()) {
                stringBuilder.append("\nНикнейм - ").append(gameUser.getName());
            }
        }
        if (getMiners().size() > 0) {
            stringBuilder.append("\n\nМайнеры:");
            int count = 1;
            for (Miner miner : getMiners()) {
                String space = Util.getStingCount(Util.getSection(count) + 3);
                stringBuilder.append("\n").append(count).append(". Название - ").append(miner.getTitle())
                        .append("\n").append(space).append("Тип - ").append(miner.getType())
                        .append("\n").append(space).append("Владелец - ").append(miner.getOwner().getName());
                count++;
            }
        }
        return stringBuilder.toString();
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
