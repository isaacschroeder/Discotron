package isaacjschroeder.discotron.data;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;
import isaacjschroeder.discotron.ObjectBox;


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

    //to get specific match par based on its hole number
    public MatchParModel getMatchPar(int number) {
        for (MatchParModel matchPar : matchPars) {
            if (matchPar.getNumber() == number)
                return matchPar;
        }
        return null;
    }

    //to set specific match par based on its hole number
    public void setMatchPar(int number, int matchPar) {
        for (MatchParModel mp : matchPars) {
            if (mp.getNumber() == number) {
                mp.setPar(matchPar);
                ObjectBox.get().boxFor(MatchParModel.class).put(mp);
            }
        }
    }

    //give a scorecard id and a score and will assign to player's scorecard
    public void setPlayerScore(long id, int number, int score) {
        for (ScoreCardModel scoreCard: scoreCards) {
            if (scoreCard.id == id)
                scoreCard.setScore(number, score);
        }
    }

    //based on playernames
    public long getScoreCardIDFromPlayerName(String name) {
        for (ScoreCardModel scoreCard: scoreCards) {
            if (scoreCard.player.getTarget().name == name)
                return scoreCard.id;
        }
        return ObjectBox.INVALID_ID;
    }

    public int calcTotalMatchPar() {
        int total = 0;
        for (MatchParModel mp : matchPars)
            total += mp.getPar();
        return total;
    }
}

