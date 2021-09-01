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
import isaacjschroeder.discotron.data.ScoreCardModel;


public class ScoreCardView extends LinearLayout {

    private TextView headerTV;
    private List<TextView> scoreTVs;

    public static final int HOLE_NUMBER_COLUMN = 0;
    public static final int HOLE_PAR_COLUMN = 1;
    public static final int PLAYER_SCORE_COLUMN = 2;

    private final int TEXT_SIZE = 24;

    public ScoreCardView(Context context, int columnType, String header, CourseModel course, ScoreCardModel playerScore) {
        super(context);
        init(context, columnType, header, course, playerScore);
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
    private void init(Context context, int columnType , String header, CourseModel course, ScoreCardModel playerScore) {
        //Setup linear layout
        this.setOrientation(LinearLayout.VERTICAL);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams headerTVParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        headerTV = new TextView(context);
        headerTV.setLayoutParams(headerTVParams);
        headerTV.setText(header);
        headerTV.setPadding(10,10,10,10);
        headerTV.setTextColor(getResources().getColor(R.color.black));
        headerTV.setBackgroundColor(getResources().getColor(R.color.grey));
        headerTV.setTextSize(TEXT_SIZE);
        headerTV.setSingleLine(true);
        this.addView(headerTV);


        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        LinearLayout.LayoutParams scoreTVParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        scoreTVParams.setMargins(5,5,5,5);
        scoreTVs = new ArrayList<TextView>();

        //iterate through each hole (-1)
        for (int i = 0; i < course.holes.size(); i++) {
            TextView score = new TextView(context);
            score.setLayoutParams(scoreTVParams);
            score.setGravity(Gravity.CENTER);
            if (columnType == HOLE_NUMBER_COLUMN)                                //just a holecount scorecard
                score.setText(String.valueOf(i + 1));
            else if (columnType == HOLE_PAR_COLUMN)                             //just a par scorecard *NEWFIX - was pars==null dumbhead
                score.setText(String.valueOf(course.getHole(i + 1).getPar()));
            else {                                                              //then is a player scorecard
                //Color based on ranges
                int ps = playerScore.getScore(i + 1).getScore();
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
            score.setPadding(5,5,5,5);
            score.setTextColor(getResources().getColor(R.color.black));
            //if (i % 2 == 0)
            //    score.setBackgroundColor(getResources().getColor(R.color.white));
            //else
            //    score.setBackgroundColor(getResources().getColor(R.color.grey));
            score.setTextSize(TEXT_SIZE);
            score.setSingleLine(true);

            FrameLayout fl = new FrameLayout(context);
            fl.setLayoutParams(frameLayoutParams);
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
