package isaacjschroeder.discotron.data;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;


@Entity
public class GameModel {

    @Id public long id;

    public ToOne<CourseModel> course;                   //one course per game

    @Backlink (to = "game")
    public ToMany<ScoreCardModel> scoreCards;           //many scoreCards per game

    @Backlink (to = "game")
    public ToMany<MatchParModel> matchPars;             //many matchPars per game


    //Game general info
    public String name;
    private String date;



    //Should contain hole data, incase different from course default! WHAT TO DO ABOUT THIS????
    //maybe boolean and hole info to determine if differs from default hole pars and specify game specific pars
    //public ToOne<ScoreCardModel> matchPars; maybe add one extra score model? no needs a player.
    //MAYBE ADD NEW DATATYPE CALLED MATCH PARS


    public GameModel() {}

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}

