package isaacjschroeder.discotron.customviews;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import isaacjschroeder.discotron.data.CourseModel;
import isaacjschroeder.discotron.data.ScoreCardModel;


public class ScoreBoardView extends LinearLayout {
    private ScoreCardView holeScoreCardView;
    private ScoreCardView parScoreCardView;
    private List<ScoreCardView> playerScoreCardViews;//is this necessary?, could just redraw whole thing if change is made. Keep if add an update function
    //private List<Integer> pars; //necessary?
    //private List<List<Integer>> playerScores; //necessary? probaly pass all info to update (but is that bad?)

    //Can i just pass this a scorecard model?
    public ScoreBoardView(Context context, List<ScoreCardModel> scoreCards, CourseModel course) {
        super(context);
        init(context, scoreCards, course);
    }

    private void init(Context context, List<ScoreCardModel> scoreCards, CourseModel course) {

        this.setOrientation(LinearLayout.HORIZONTAL);
        //Need the width of this or its parent to scale based on number of holes!
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        //Create hole number card - could pass scorecard view a property to make text different than players (highlight scores for players, but not hole or par)
        holeScoreCardView = new ScoreCardView(context, ScoreCardView.HOLE_NUMBER_COLUMN, "Hole", course, null);
        this.addView(holeScoreCardView);

        //Create par card
        parScoreCardView = new ScoreCardView(context, ScoreCardView.HOLE_PAR_COLUMN, "Par", course, null);
        this.addView(parScoreCardView);

        playerScoreCardViews = new ArrayList<ScoreCardView>(); //is this necessary?

        //Create player entries
        if (scoreCards != null) {
            for (int i = 0; i < scoreCards.size(); i++) {
                ScoreCardView scoreCard = new ScoreCardView(context, ScoreCardView.PLAYER_SCORE_COLUMN, "playa name", course, scoreCards.get(i));
                this.addView(scoreCard);
                playerScoreCardViews.add(scoreCard); //NEWFIX//is this necessary?
            }
        }
    }


    public void updateScoreEntry(int scoreCard, int holeNumber, int score)
    {
        if (scoreCard == 0) //then editing pars
        {
            parScoreCardView.updateText(ScoreCardView.HOLE_PAR_COLUMN, holeNumber, score);
        }
        else {
            //for player score
        }
    }

    //add player?
    //if holecount changes, can probly just delete subviews and reform?


}
