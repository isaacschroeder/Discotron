package isaacjschroeder.discotron.controllers;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.NumberPicker;

import isaacjschroeder.discotron.ObjectBox;
import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.SharedPreferencesManager;
import isaacjschroeder.discotron.customviews.ScoreBoardView;
import isaacjschroeder.discotron.data.GameModel;

public class GamePlayActivity extends AppCompatActivity {

    private Button enterScoreBTN;
    private NumberPicker holeSelectNP;
    private HorizontalScrollView hscroll;
    private ScoreBoardView gameSBV;

    private GameModel game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get game id
        Intent i = getIntent();
        long id = i.getLongExtra(ObjectBox.ID_EXTRA, ObjectBox.INVALID_ID);

        //get the game
        if (id != ObjectBox.INVALID_ID)
            game = ObjectBox.get().boxFor(GameModel.class).get(id);

        setTitle("Playing " + game.name);

        //find views
        enterScoreBTN = findViewById(R.id.game_play_enterscore_btn);
        holeSelectNP = findViewById(R.id.game_play_holeselect_np);
        hscroll = findViewById(R.id.game_play_scoreboard_hscroll);

        gameSBV = new ScoreBoardView(this, game);
        hscroll.addView(gameSBV);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_game_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_game_play_action_finish:
                //END GAME

                //ensure user wants to end the game via alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(GamePlayActivity.this);
                builder.setMessage("End Game?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //clear game in progress
                        SharedPreferencesManager.write(SharedPreferencesManager.GAME_IN_PROGRESS_ID, ObjectBox.INVALID_ID);
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            case android.R.id.home:
                //SAVE INFO IN SHARED PREFS
                saveGameInfo();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //SAVE INFO IN SHARED PREFS
        saveGameInfo();
        super.onBackPressed();
    }

    private void saveGameInfo() {
        SharedPreferencesManager.write(SharedPreferencesManager.GAME_IN_PROGRESS_ID, game.id);
    }
}