package isaacjschroeder.discotron.customviews;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.data.CourseModel;
import isaacjschroeder.discotron.data.GameModel;
import isaacjschroeder.discotron.data.ScoreCardModel;


public class ScoreCardView extends LinearLayout {

    private TextView headerTV;
    private List<TextView> scoreTVs;

    public static final int HOLE_NUMBER_COLUMN = 0;
    public static final int HOLE_PAR_COLUMN = 1;
    public static final int HOLE_MATCH_PAR_COLUMN = 2;
    public static final int PLAYER_SCORE_COLUMN = 3;

    private final int TEXT_SIZE = 24;

    private final String SCORE_NOT_SET_SYMBOL = "~";

    public ScoreCardView(Context context, int columnType, String header, CourseModel course, ScoreCardModel playerScore, GameModel game) {
        super(context);
        init(context, columnType, header, course, playerScore, game);
    }


    //make sure color rules apply
    public void updateText(int columnType, int holeNumber, int score) {
        if (columnType == HOLE_PAR_COLUMN)
        {
            scoreTVs.get(holeNumber).setText(String.valueOf(score));
        }
        else{
            //apply color rules
        }
    }


    //CHANGE ORIENTATION?
    private void init(Context context, int columnType , String header, CourseModel course, ScoreCardModel playerScore, GameModel game) {
        //Setup linear layout
        this.setOrientation(LinearLayout.VERTICAL);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams headerTVParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        headerTV = new TextView(context);
        headerTV.setLayoutParams(headerTVParams);
        headerTV.setText(header);
        headerTV.setPadding(20,20,20,20);
        headerTV.setTextColor(getResources().getColor(R.color.black));
        headerTV.setBackgroundColor(getResources().getColor(R.color.grey));
        headerTV.setTextSize(TEXT_SIZE);
        headerTV.setSingleLine(true);
        this.addView(headerTV);

        //For setting score tvs behavior in their frame layout cells
        FrameLayout.LayoutParams scoreTVParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        scoreTVParams.setMargins(10,10,10,10);

        scoreTVs = new ArrayList<TextView>();

        //iterate through each hole (-1)
        for (int i = 0; i < course.holes.size(); i++) {
            TextView score = new TextView(context);
            score.setLayoutParams(scoreTVParams);
            if (columnType == HOLE_NUMBER_COLUMN)                                //just a holecount scorecard
                score.setText(String.valueOf(i + 1));
            else if (columnType == HOLE_PAR_COLUMN) {                            //just a par scorecard
                score.setText(String.valueOf(course.getHole(i + 1).getPar()));
            }
            else if (columnType == HOLE_MATCH_PAR_COLUMN) {                     //just a par scorecard, but has info about default par and the current match par
                int defaultPar = course.getHole(i + 1).getPar();
                int matchPar = game.getMatchPar(i + 1).getPar();
                if (defaultPar != matchPar) {
                    score.setText(String.valueOf(matchPar) + " (" + String.valueOf(defaultPar) + ")");
                }
                else {
                    score.setText(String.valueOf(matchPar));
                }
            }
            else {                                                              //then is a player scorecard

                int ps = playerScore.getScore(i + 1).getScore();

                //if score is -1, then display symbol signifying a score has not been made yet
                if (ps == -1) {
                    score.setText(SCORE_NOT_SET_SYMBOL);
                }
                else {
                    //set player score text
                    score.setText(String.valueOf(ps));

                    //Color based on ranges
                    int par = course.getHole(i + 1).getPar();
                    if (ps == 1)
                        score.setBackgroundColor(getResources().getColor(R.color.ace));
                    else if (par - ps > 2)
                        score.setBackgroundColor(getResources().getColor(R.color.albatross_or_better));
                    else if (par - ps == 2)
                        score.setBackgroundColor(getResources().getColor(R.color.eagle));
                    else if (par - ps == 1)
                        score.setBackgroundColor(getResources().getColor(R.color.birdie));
                    else if (par - ps == -1)
                        score.setBackgroundColor(getResources().getColor(R.color.bogey));
                    else if (par - ps == -2)
                        score.setBackgroundColor(getResources().getColor(R.color.double_bogey));
                    else if (par - ps < -2)
                        score.setBackgroundColor(getResources().getColor(R.color.triple_bogey_or_worse));
                }
            }
            score.setPadding(5,5,5,5);
            score.setTextColor(getResources().getColor(R.color.black));
            score.setTextSize(TEXT_SIZE);
            score.setSingleLine(true);

            FrameLayout fl = new FrameLayout(context);
            if (i % 2 == 0)
                fl.setBackgroundColor(getResources().getColor(R.color.white));
            else
                fl.setBackgroundColor(getResources().getColor(R.color.grey));

            fl.addView(score);
            this.addView(fl);            //add to linear layout
            scoreTVs.add(i, score);         //add to scoreTVs list
        }
    }
}
