package isaacjschroeder.discotron.controllers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.objectbox.Box;
import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.adapters.GameRVAdapter;
import isaacjschroeder.discotron.data.GameModel;

public class GameListActivity extends AppCompatActivity {

    private RecyclerView rv;
    private GameRVAdapter rvAdapter;
    private Box<GameModel> gameBox;

    private static final int SORT_AZ = 0;
    private static final int SORT_ZA = 1;
    private static final int SORT_GP = 2;
    private int sortingMethod = SORT_AZ;

    //for determining purpose of launching this activity to behave accordingly.
    //is passed in from calling activity as an int extra
    public static final String GAME_LIST_USE_EXTRA = "GAME_LIST_USE_EXTRA";
    public static final int GAME_LIST_USE_VIEW_DETAILS = 0;
    public static final int GAME_LIST_USE_SELECT_AND_RETURN_ID = 1;

    public static final String GAME_LIST_GAME_ID_EXTRA = "GAME_LIST_GAME_ID_EXTRA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup RecyclerView
        rv = findViewById(R.id.game_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(GameListActivity.this));

        //Determine list use
        Intent intent = getIntent();
        int listUse = intent.getIntExtra(GAME_LIST_USE_EXTRA, GAME_LIST_USE_SELECT_AND_RETURN_ID);
        if (listUse == GAME_LIST_USE_VIEW_DETAILS) {
            setTitle("Games");
            rvAdapter = new GameRVAdapter(gameBox.getAll(), new GameRVAdapter.OnGameClickListener() {
                @Override
                public void onGameClick(View itemView, int position) {
                    //Launch view game details activity
                    Intent i = new Intent(GameListActivity.this, GameDetailsActivity.class);

                }
            });
        }
        else {

        }
    }
}