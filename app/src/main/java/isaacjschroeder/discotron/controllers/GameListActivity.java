package isaacjschroeder.discotron.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import io.objectbox.Box;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import isaacjschroeder.discotron.ObjectBox;
import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.adapters.GameRVAdapter;
import isaacjschroeder.discotron.data.CourseModel;
import isaacjschroeder.discotron.data.CourseModel_;
import isaacjschroeder.discotron.data.GameModel;
import isaacjschroeder.discotron.data.GameModel_;

public class GameListActivity extends AppCompatActivity {

    private RecyclerView rv;
    private GameRVAdapter rvAdapter;
    private Box<GameModel> gameBox;

    private static final int SORT_AZ = 0;
    private static final int SORT_ZA = 1;
    private static final int SORT_DATE = 2;
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

        //Get box for games
        gameBox = ObjectBox.get().boxFor(GameModel.class);

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
                    i.putExtra(ObjectBox.ID_EXTRA, rvAdapter.getGameID(position));
                    startActivity(i);
                }
            });
        }
        else {
            setTitle("Select a Game");
            rvAdapter = new GameRVAdapter(gameBox.getAll(), new GameRVAdapter.OnGameClickListener() {
                @Override
                public void onGameClick(View itemView, int position) {
                    //Finish activity, returning the selected game id to be used
                    Intent passBackId = new Intent();
                    passBackId.putExtra(GAME_LIST_GAME_ID_EXTRA, rvAdapter.getGameID(position));
                    setResult(RESULT_OK, passBackId);
                    finish();
                }
            });
        }

        rv.setAdapter(rvAdapter);

        updateSortedList();
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

    private void updateSortedList() {
        Query<GameModel> query;
        switch (sortingMethod) {
            case SORT_AZ:
                query = gameBox.query().order(GameModel_.name).build();
                break;
            case SORT_ZA:
                query = gameBox.query().order(GameModel_.name, QueryBuilder.DESCENDING).build();
                break;
            case SORT_DATE:
                query = gameBox.query().order(GameModel_.date).build();
                break;
            default:
                query = gameBox.query().build();
                break;
        }
        rvAdapter.setGamesList(query.find());
    }
}