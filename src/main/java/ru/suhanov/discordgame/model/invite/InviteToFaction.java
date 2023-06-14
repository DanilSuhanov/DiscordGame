package ru.suhanov.discordgame.model.invite;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.suhanov.discordgame.model.GameUser;

@Data
@NoArgsConstructor
@Entity
public class InviteToFaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private GameUser from;

    @ManyToOne
    private GameUser to;

    public InviteToFaction(GameUser from, GameUser to) {
        this.from = from;
        this.to = to;
    }
}
