package isaacjschroeder.discotron.data;

import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;
import isaacjschroeder.discotron.ObjectBox;

@Entity
public class ScoreCardModel {

    @Id public long id;

    @Backlink(to = "scoreCard")
    public ToMany<ScoreModel> scores;           //Many scores per scorecard
    public ToOne<GameModel> game;               //One game per scorecard
    public ToOne<PlayerModel> player;           //One player per scorecard


    public ScoreCardModel() {}

    public ScoreModel getScore(int holeNumber) {
        for (ScoreModel score : scores) {
            if (score.getHoleNumber() == holeNumber)
                return score;
        }
        return null;
    }

    public void setScore(int holeNumber, int score) {
        for (ScoreModel s : scores) {
            if (s.getHoleNumber() == holeNumber) {
                s.setScore(score);
                ObjectBox.get().boxFor(ScoreModel.class).put(s);
            }
        }
    }
}
