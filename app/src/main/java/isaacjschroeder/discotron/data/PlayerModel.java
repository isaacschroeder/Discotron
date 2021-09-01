package isaacjschroeder.discotron.data;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class PlayerModel {

    @Id public long id;

    //Relations
    @Backlink (to = "player")
    public ToMany<ScoreCardModel> cards;         //Many cards per player


    public String name; //BE SURE TO ENFORCE THAT EACH PLAYER HAS UNIQUE NAME
    public int gamesPlayed;


    public PlayerModel() {}
    public PlayerModel(String name) {
        this.name = name;
        gamesPlayed = 0;
    }
}
