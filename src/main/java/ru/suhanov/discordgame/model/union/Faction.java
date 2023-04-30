package ru.suhanov.discordgame.model.union;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.suhanov.discordgame.model.GameUser;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Faction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String description;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private GameUser leader;

    @OneToMany(mappedBy = "faction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GameUser> members;

    public Faction(String title, String description, GameUser leader) {
        this.title = title;
        this.description = description;
        this.leader = leader;
    }

    public void addMember(GameUser gameUser) {
        members.add(gameUser);
    }
}
