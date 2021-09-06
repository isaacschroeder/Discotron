package isaacjschroeder.discotron.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
    //private TextView totalsTV;
    //private TextView plusMinusTV;
    private List<TextView> scoreTVs;

    private GameModel game;
    private CourseModel course;
    private ScoreCardModel playerScore;

    private long scoreCardID;                             //bad fix******* -1 if not a player scorecard should have different classes for types of scorecard columns

    public static final int HOLE_NUMBER_COLUMN = 0;
    public static final int HOLE_PAR_COLUMN = 1;
    public static final int HOLE_MATCH_PAR_COLUMN = 2;
    public static final int PLAYER_SCORE_COLUMN = 3;

    private static final int TEXT_SIZE = 24;
    private static final int HEADER_TEXT_SIZE = 28;

    private static final String SCORE_NOT_SET_SYMBOL = "~";

    public ScoreCardView(Context context, int columnType, String header, CourseModel course, ScoreCardModel playerScore, GameModel game) {
        super(context);

        //USE THESE FOR THINGS BC WHY NOT FOOL?
        this.game = game;
        this.course = course;
        this.playerScore = playerScore;

        init(context, columnType, header, course, playerScore, game);
    }

    public long getScoreCardID() {return scoreCardID;}              //BAD FIX FOR IDENTIFYING PLAYER COLUMNS

    public void updateParText(int holeNumber, int par) {
        scoreTVs.get(holeNumber).setText(String.valueOf(par));
        //totalsTV.setText(String.valueOf(course.calcTotalDefaultPar()));
    }

    //if default par != match par, then update text
    public void updateMatchParText(int holeNumber, int defaultPar, int matchPar) {
        if (defaultPar != matchPar)
            scoreTVs.get(holeNumber).setText(String.valueOf(matchPar) + " (" + String.valueOf(defaultPar) + ")");
        else
            scoreTVs.get(holeNumber).setText(String.valueOf(defaultPar));

        //totalsTV.setText(String.valueOf(game.calcTotalMatchPar()));
    }

    //make sure color rules apply
    public void updatePlayerScoreText(int holeNumber, int par, int score) {
        if (score == -1) {
            scoreTVs.get(holeNumber).setText(SCORE_NOT_SET_SYMBOL);
        }
        else {
            //set player score text
            scoreTVs.get(holeNumber).setText(String.valueOf(score));
            //Color based on ranges
            applyColorRulesToScore(score, par, scoreTVs.get(holeNumber));
            //totalsTV.setText(String.valueOf(playerScore.calcTotalScore()));
        }
    }


    //CHANGE ORIENTATION?
    private void init(Context context, int columnType, String header, CourseModel course, ScoreCardModel playerScore, GameModel game) {

        //BAD FIX FOR IDENTIFYING PLAYER COLUMNS
        if (columnType == PLAYER_SCORE_COLUMN)
            scoreCardID = playerScore.id;
        else
            scoreCardID = -1;
        //BAD FIX FOR IDENTIFYING PLAYER COLUMNS

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
        headerTV.setTextSize(HEADER_TEXT_SIZE);
        headerTV.setSingleLine(true);
        this.addView(headerTV);


        //For setting score tvs behavior in their frame layout cells
        FrameLayout.LayoutParams scoreTVParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        scoreTVParams.setMargins(10,10,10,10);

//        //New rows
//        totalsTV = new TextView(context);
//        totalsTV.setLayoutParams(scoreTVParams);
//        totalsTV.setTextColor(getResources().getColor(R.color.black));
//        totalsTV.setTextSize(TEXT_SIZE);
//        totalsTV.setSingleLine(true);
//        FrameLayout frameLayout = new FrameLayout(context);
//        frameLayout.setBackgroundColor(getResources().getColor(R.color.white));
//        frameLayout.addView(totalsTV);
//        this.addView(frameLayout);
//
//        if (columnType == HOLE_NUMBER_COLUMN)
//            totalsTV.setText("Total");
//        else if (columnType == HOLE_PAR_COLUMN)
//            totalsTV.setText(String.valueOf(course.calcTotalDefaultPar()));
//        else if (columnType == HOLE_MATCH_PAR_COLUMN)
//            totalsTV.setText(String.valueOf(game.calcTotalMatchPar()));
//        else
//            totalsTV.setText(String.valueOf(playerScore.calcTotalScore()));
//        //New rows


        scoreTVs = new ArrayList<TextView>();

        //iterate through each hole (-1)
        for (int i = 0; i < course.holes.size(); i++) {
            TextView score = new TextView(context);
            score.setLayoutParams(scoreTVParams);
            if (columnType == HOLE_NUMBER_COLUMN) {                               //just a holecount scorecard
                score.setText(String.valueOf(i + 1));
            }
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
                    score.setText(String.valueOf(defaultPar));
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
                    int par = game.getMatchPar(i + 1).getPar();
                    applyColorRulesToScore(ps, par, score);
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

    public void applyColorRulesToScore(int score, int matchPar, TextView scoreTV) {
        if (score == 1)
            scoreTV.setBackgroundColor(getResources().getColor(R.color.ace));
        else if (matchPar - score > 2)
            scoreTV.setBackgroundColor(getResources().getColor(R.color.albatross_or_better));
        else if (matchPar - score == 2)
            scoreTV.setBackgroundColor(getResources().getColor(R.color.eagle));
        else if (matchPar - score == 1)
            scoreTV.setBackgroundColor(getResources().getColor(R.color.birdie));
        else if (matchPar - score == -1)
            scoreTV.setBackgroundColor(getResources().getColor(R.color.bogey));
        else if (matchPar - score == -2)
            scoreTV.setBackgroundColor(getResources().getColor(R.color.double_bogey));
        else if (matchPar - score < -2)
            scoreTV.setBackgroundColor(getResources().getColor(R.color.triple_bogey_or_worse));
        else
            scoreTV.setBackgroundColor(Color.TRANSPARENT);
    }
}
