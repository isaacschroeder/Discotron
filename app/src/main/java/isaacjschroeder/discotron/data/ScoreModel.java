package isaacjschroeder.discotron.data;


import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class ScoreModel {

    @Id public long id;

    public ToOne<ScoreCardModel> scoreCard;

    private int holeNumber;
    private int score;


    public ScoreModel() {}
    public ScoreModel( int score, int holeNumber ) { this.score = score; this.holeNumber = holeNumber; }


    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public int getHoleNumber() { return holeNumber; }
    public void setHoleNumber(int holeNumber) { this.holeNumber = holeNumber; }
}
