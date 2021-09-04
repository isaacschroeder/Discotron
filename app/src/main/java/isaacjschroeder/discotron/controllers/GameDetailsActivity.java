package isaacjschroeder.discotron.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import isaacjschroeder.discotron.ObjectBox;
import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.customviews.ScoreBoardView;
import isaacjschroeder.discotron.data.GameModel;

public class GameDetailsActivity extends AppCompatActivity {

    private TextView gameNameTV;
    private TextView courseNameTV;
    private TextView playerNamesTV;
    private HorizontalScrollView hscroll;
    private ScoreBoardView scoreBoard;

    private GameModel game;

    //doesn't have a menu like the other detail activities, but may need one to add delete feature

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        setTitle("Game Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        long id = i.getLongExtra(ObjectBox.ID_EXTRA, ObjectBox.INVALID_ID);

        game = ObjectBox.get().boxFor(GameModel.class).get(id);

        gameNameTV = findViewById(R.id.game_details_name_tv);
        courseNameTV = findViewById(R.id.game_details_coursename_tv);
        playerNamesTV = findViewById(R.id.game_details_playernames_tv);
        hscroll = findViewById(R.id.game_details_scoreboard_hscroll);

        scoreBoard = new ScoreBoardView(this, game.scoreCards, game.course.getTarget());
        hscroll.addView(scoreBoard);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}