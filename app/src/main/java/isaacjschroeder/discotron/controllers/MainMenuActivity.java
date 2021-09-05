package isaacjschroeder.discotron.controllers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import isaacjschroeder.discotron.MainApplication;
import isaacjschroeder.discotron.ObjectBox;
import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.SharedPreferencesManager;

public class MainMenuActivity extends AppCompatActivity {

    private Button newOrContinueBTN;
    private Button gamesBTN;
    private Button playersBTN;
    private Button coursesBTN;
    private Button statsBTN;

    private static final int GAME_CREATE_RC = 0;

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Link all UI elements
        newOrContinueBTN =  findViewById(R.id.main_play_btn);
        gamesBTN =          findViewById(R.id.main_games_btn);
        playersBTN =        findViewById(R.id.main_players_btn);
        coursesBTN =        findViewById(R.id.main_courses_btn);
        statsBTN =          findViewById(R.id.main_stats_btn);
        iv =                findViewById(R.id.main_iv);

        //Set correct text on newOrContinueBTN
        if (SharedPreferencesManager.read(SharedPreferencesManager.GAME_IN_PROGRESS_ID, ObjectBox.INVALID_ID) != ObjectBox.INVALID_ID)
            newOrContinueBTN.setText("Continue Game");
        else
            newOrContinueBTN.setText("New Game");

        //Main menu image - logo for now
        iv.setImageResource(R.drawable.discotron_logo);

        //Click events for all main menu buttons
        playersBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, PlayersListActivity.class);
                i.putExtra(PlayersListActivity.PLAYER_LIST_USE_EXTRA, PlayersListActivity.PLAYER_LIST_USE_VIEW_DETAILS);   //start list activity for browsing player details
                startActivity(i);
            }
        });
        coursesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, CourseListActivity.class);
                i.putExtra(CourseListActivity.COURSE_LIST_USE_EXTRA, CourseListActivity.COURSE_LIST_USE_VIEW_DETAILS);   //start list activity for browsing course details
                startActivity(i);
            }
        });

        newOrContinueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPreferencesManager.read(SharedPreferencesManager.GAME_IN_PROGRESS_ID, ObjectBox.INVALID_ID) == ObjectBox.INVALID_ID) {
                    //Make a new game
                    Intent i = new Intent(MainMenuActivity.this, GameCreateActivity.class);
                    startActivityForResult(i, GAME_CREATE_RC);
                } else {
                    //Continue a current game
                    Intent i = new Intent(MainMenuActivity.this, GamePlayActivity.class);
                    i.putExtra(ObjectBox.ID_EXTRA, SharedPreferencesManager.read(SharedPreferencesManager.GAME_IN_PROGRESS_ID, ObjectBox.INVALID_ID));
                    startActivity(i);
                }
            }
        });

        gamesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, GameListActivity.class);
                i.putExtra(GameListActivity.GAME_LIST_USE_EXTRA, GameListActivity.GAME_LIST_USE_VIEW_DETAILS);   //start list activity for browsing game details
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_delete_all_data:
                //Delete all data! Scary mode - make sure that no objectbox is being interacted with during this!
                ObjectBox.get().close();
                ObjectBox.get().deleteAllFiles();
                ObjectBox.init(getApplicationContext());

                //ALSO NEED TO CLEAR SHAREDPREFS!!! no game should be in progress after this
                SharedPreferencesManager.clearData();

                //NEED TO RESET CONTINUE GAME BUTTON TO NEW GAME BC THERE WILL BE NO GAME TO CONTINUE AFTER THIS
                newOrContinueBTN.setText("New Game");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SharedPreferencesManager.read(SharedPreferencesManager.GAME_IN_PROGRESS_ID, ObjectBox.INVALID_ID) != ObjectBox.INVALID_ID)
            newOrContinueBTN.setText("Continue Game");
        else
            newOrContinueBTN.setText("New Game");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GAME_CREATE_RC && resultCode == RESULT_OK) {
            long id = data.getLongExtra(ObjectBox.ID_EXTRA, ObjectBox.INVALID_ID);

            //Launch newly created game passed back from game create activity
            Intent i = new Intent(MainMenuActivity.this, GamePlayActivity.class);
            i.putExtra(ObjectBox.ID_EXTRA, id);
            startActivity(i);
        }
    }
}