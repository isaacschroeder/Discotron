package isaacjschroeder.discotron.data;

import android.util.Log;

import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class CourseModel {

    @Id public long id;

    @Backlink(to = "course")
    public ToMany<HoleModel> holes;           //Many holes per course

    @Backlink(to = "course")
    public ToMany<GameModel> game;            //Many games per course


    public String name; //enforce unique name on courses also probly
    public int gamesPlayed; //could be calculated based on related games...


    public CourseModel() { gamesPlayed = 0; }
    public CourseModel(String name) {
        this.name = name;
        gamesPlayed = 0;
    }

    //to get specific hole based on its hole number
    public HoleModel getHole(int number) {
        for (HoleModel hole : holes) {
            if (hole.getNumber() == number)
                return hole;
        }
        return null;
    }

    //should add a set hole function for easy updating and putting instead of doing it all externally
}
