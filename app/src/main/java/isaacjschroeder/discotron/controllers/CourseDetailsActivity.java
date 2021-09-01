package isaacjschroeder.discotron.controllers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import isaacjschroeder.discotron.ObjectBox;
import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.customviews.ScoreBoardView;
import isaacjschroeder.discotron.customviews.ScoreCardView;
import isaacjschroeder.discotron.data.CourseModel;
import isaacjschroeder.discotron.data.PlayerModel;

public class CourseDetailsActivity extends AppCompatActivity {

    private TextView nameTV;
    private TextView gamesPlayedTV;
    private TextView holeCountTV;
    private ScrollView vscroll;
    private ScoreBoardView defaultParsSBV;

    private long id;
    private int editResult = RESULT_CANCELED;
    private CourseModel c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        setTitle("Course Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        id = i.getLongExtra(ObjectBox.ID_EXTRA, ObjectBox.INVALID_ID);

        c = ObjectBox.get().boxFor(CourseModel.class).get(id);

        nameTV = findViewById(R.id.course_detials_name_tv);
        gamesPlayedTV = findViewById(R.id.course_detials_gamecount_tv);
        holeCountTV = findViewById(R.id.course_detials_holecount_tv);
        nameTV.setText("Name: " + c.name);
        gamesPlayedTV.setText("Games Played: " + c.gamesPlayed); //calculate with a query instead?
        holeCountTV.setText("Hole Count: " + c.holes.size());

        //Attempt at scorecard shenanigans
        vscroll = findViewById(R.id.course_details_vscroll);

        defaultParsSBV = new ScoreBoardView(this, null, c);
        vscroll.addView(defaultParsSBV);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_details_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_details_options_action_edit:
                //start edit player activity
                Intent i = new Intent(CourseDetailsActivity.this, CourseAddActivity.class);
                i.putExtra(ObjectBox.ID_EXTRA, id);
                startActivityForResult(i, PlayerAddActivity.EDIT_PLAYER_RC);
                return true;
            case android.R.id.home:
                setResult(editResult);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(editResult);
        super.onBackPressed();
    }

    //pass result code for calling activity (playerlistactivity) to determine if item was edited and update visuals if so
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if ok then edit occured
        if (resultCode == RESULT_OK)
        {
            //fetch updated coursemodel
            c = ObjectBox.get().boxFor(CourseModel.class).get(id);
            nameTV.setText("Name: " + c.name);

            //delete and remake scoreboard view to show edits
            vscroll.removeView(defaultParsSBV);
            defaultParsSBV = new ScoreBoardView(this, null, c);
            vscroll.addView(defaultParsSBV);
        }
        editResult = resultCode;
    }
}