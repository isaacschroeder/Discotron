package isaacjschroeder.discotron.customviews;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import isaacjschroeder.discotron.data.CourseModel;
import isaacjschroeder.discotron.data.GameModel;
import isaacjschroeder.discotron.data.ScoreCardModel;


public class ScoreBoardView extends LinearLayout {
    private ScoreCardView holeScoreCardView;
    private ScoreCardView parScoreCardView;
    private List<ScoreCardView> playerScoreCardViews;//is this necessary?, could just redraw whole thing if change is made. Keep if add an update function


    //Constructor for game info scoreboard
    public ScoreBoardView(Context context, List<ScoreCardModel> scoreCards, CourseModel course, GameModel game) {
        super(context);
        init(context, scoreCards, course, game);
    }

    //Constructor for course info scoreboard
    public ScoreBoardView(Context context, CourseModel course) {
        super(context);
        init(context, null, course, null);
    }

    private void init(Context context, List<ScoreCardModel> scoreCards, CourseModel course, GameModel game) {

        this.setOrientation(LinearLayout.HORIZONTAL);
        //Need the width of this or its parent to scale based on number of holes!
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        //Create hole number card - could pass scorecard view a property to make text different than players (highlight scores for players, but not hole or par)
        holeScoreCardView = new ScoreCardView(context, ScoreCardView.HOLE_NUMBER_COLUMN, "Hole", course, null, null);
        this.addView(holeScoreCardView);

        //Create par card
        if (scoreCards == null)
            parScoreCardView = new ScoreCardView(context, ScoreCardView.HOLE_PAR_COLUMN, "Par", course, null, null);
        else
            parScoreCardView = new ScoreCardView(context, ScoreCardView.HOLE_MATCH_PAR_COLUMN, "Par", course, null, game);
        this.addView(parScoreCardView);

        playerScoreCardViews = new ArrayList<ScoreCardView>(); //is this necessary?

        //Create player entries
        if (scoreCards != null) {
            for (int i = 0; i < scoreCards.size(); i++) {
                ScoreCardView scoreCard = new ScoreCardView(context, ScoreCardView.PLAYER_SCORE_COLUMN, scoreCards.get(i).player.getTarget().name, course, scoreCards.get(i), null);
                this.addView(scoreCard);
                playerScoreCardViews.add(scoreCard); //NEWFIX//is this necessary?
            }
        }
    }


    public void updateScoreEntry(int scoreCard, int holeNumber, int score)
    {
        if (scoreCard == 0) //then editing pars
        {
            parScoreCardView.updateParText(holeNumber, score);
        }
        else {
            //for player score
        }
    }

    //add player?
    //if holecount changes, can probly just delete subviews and reform?


}
