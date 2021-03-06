package isaacjschroeder.discotron.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

        gameNameTV.setText("Name: " + game.name);
        courseNameTV.setText("Course: " + game.course.getTarget().name);
        playerNamesTV.setText("Players: " + game.scoreCards.size()); //For now just list number of players
//        for (int x = 0; x < game.scoreCards.size(); x++) {
//            playerNamesTV.setText(playerNamesTV.getText().toString() + " " + game.scoreCards.get(x).player.getTarget().name);
//        }

        scoreBoard = new ScoreBoardView(this, game);
        hscroll.addView(scoreBoard);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}