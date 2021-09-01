package isaacjschroeder.discotron.controllers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import isaacjschroeder.discotron.ObjectBox;
import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.adapters.PlayerRVAdapter;
import isaacjschroeder.discotron.data.PlayerModel;
import isaacjschroeder.discotron.data.PlayerModel_;

public class PlayersListActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    private Button returnSelectedBTN; //for returning multiple ids

    private RecyclerView rv;
    private PlayerRVAdapter rvAdapter;
    private Box<PlayerModel> playersBox;


    private static final int SORT_AZ = 0;
    private static final int SORT_ZA = 1;
    private static final int SORT_GP = 2;
    private int sortingMethod = SORT_AZ;


    //for determining purpose of launching this activity to behave accordingly.
    //is passed in from calling activity as an int extra
    public static final String PLAYER_LIST_USE_EXTRA = "PLAYER_LIST_USE_EXTRA";
    public static final int PLAYER_LIST_USE_VIEW_DETAILS = 0;
    public static final int PLAYER_LIST_USE_SELECT_AND_RETURN_ONE_ID = 1;
    public static final int PLAYER_LIST_USE_SELECT_AND_RETURN_MULTIPLE_IDS = 2;

    public static final String PLAYER_LIST_PLAYER_ID_EXTRA = "PLAYER_LIST_PLAYER_ID_EXTRA";
    public static final String PLAYER_LIST_PLAYER_MULTIPLE_IDS_EXTRA = "PLAYER_LIST_PLAYER_MULTIPLE_IDS_EXTRA";

    public static final int MAX_SELECTABLE_PLAYERS = 10;


    //only used if activity was started to return multiple players
    private List<Long> playerIDs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get box for players
        playersBox = ObjectBox.get().boxFor(PlayerModel.class);

        //Fab add player button
        fab = findViewById(R.id.player_list_add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlayersListActivity.this, PlayerAddActivity.class);
                i.putExtra(ObjectBox.ID_EXTRA, ObjectBox.INVALID_ID);
                startActivityForResult(i, PlayerAddActivity.ADD_PLAYER_RC);
            }
        });

        //Setup RecyclerView
        rv = findViewById(R.id.player_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(PlayersListActivity.this));

        returnSelectedBTN = findViewById(R.id.player_list_return_selected_btn);

        //Determine list use and setup click listeners for rv items accordingly
        Intent intent = getIntent();
        int listUse = intent.getIntExtra(PLAYER_LIST_USE_EXTRA, PLAYER_LIST_USE_SELECT_AND_RETURN_ONE_ID);
        if (listUse == PLAYER_LIST_USE_VIEW_DETAILS) {

            //disable bc not needed
            returnSelectedBTN.setVisibility(View.GONE);
            returnSelectedBTN.setEnabled(false);

            setTitle("Players");
            rvAdapter = new PlayerRVAdapter(playersBox.getAll(), new PlayerRVAdapter.OnPlayerClickListener() {
                @Override
                public void onPlayerClick(View itemView, int position) {
                    //Launch view player details activity
                    Intent i = new Intent(PlayersListActivity.this, PlayerDetailsActivity.class);
                    i.putExtra(ObjectBox.ID_EXTRA, rvAdapter.getPlayerId(position));
                    startActivityForResult(i, PlayerAddActivity.EDIT_PLAYER_RC);
                }
            });
        }
        else if (listUse == PLAYER_LIST_USE_SELECT_AND_RETURN_MULTIPLE_IDS) {
            setTitle("Select Players");

            playerIDs = new ArrayList<>();

            rvAdapter = new PlayerRVAdapter(playersBox.getAll(), new PlayerRVAdapter.OnPlayerClickListener() {
                @Override
                public void onPlayerClick(View itemView, int position) {
                    //Toggle whether the player is selected or not for returning, adding or removing them from the id list
                    TextView playerNameTV = itemView.findViewById(R.id.rvah_player_name_tv);

                    //If the player is not selected currently, select them, highlighting text and adding them to the player ids list
                    long playerID = rvAdapter.getPlayerId(position);
                    if (!playerIDsContainID(playerID)) {
                        //Select a max of 10 players
                        if (playerIDs.size() < MAX_SELECTABLE_PLAYERS) {
                            playerNameTV.setTextColor(getResources().getColor(R.color.selected));
                            playerIDs.add(playerID);
                        }
                        else {
                            Toast.makeText(getParent(), "You can select a max of 10 players fool!", Toast.LENGTH_LONG).show();
                        }
                    }
                    //If the player is selected, remove them from the selected player ids list
                    else {
                        playerNameTV.setTextColor(getResources().getColor(R.color.black));
                        playerIDs.remove(playerID);
                    }
                }
            });

            returnSelectedBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //convert to array to be passed through intent
                    long[] playerIDsArr = new long[playerIDs.size()];
                    int x = 0;
                    for (Long l : playerIDs)
                        playerIDsArr[x++] = l.longValue();

                    Intent passBackIds = new Intent();
                    passBackIds.putExtra(PLAYER_LIST_PLAYER_MULTIPLE_IDS_EXTRA, playerIDsArr);
                    setResult(RESULT_OK, passBackIds);
                    finish();
                }
            });
        }
        else {

            //disable bc not needed
            returnSelectedBTN.setVisibility(View.GONE);
            returnSelectedBTN.setEnabled(false);

            setTitle("Select Player");
            rvAdapter = new PlayerRVAdapter(playersBox.getAll(), new PlayerRVAdapter.OnPlayerClickListener() {
                @Override
                public void onPlayerClick(View itemView, int position) {
                    //Finish activity, returning the single player id
                    Intent passBackId = new Intent();
                    passBackId.putExtra(PLAYER_LIST_PLAYER_ID_EXTRA, rvAdapter.getPlayerId(position));
                    setResult(RESULT_OK, passBackId);
                    finish();
                }
            });
        }

        rv.setAdapter(rvAdapter);

        //Initially sort by games played?
        updateSortedList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_player_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_player_list_action_sort_az:
                sortingMethod = SORT_AZ;
                break;
            case R.id.menu_player_list_action_sort_za:
                sortingMethod = SORT_ZA;
                break;
            case R.id.menu_player_list_action_sort_gp:
                sortingMethod = SORT_GP;
                break;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        updateSortedList();
        return true;
    }

    //only update playerslist in adapter if result is RESULT_OK, meaning there were actually box changes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            updateSortedList();
        }
    }

    private void updateSortedList() {
        Query<PlayerModel> query;
        switch (sortingMethod) {
            case SORT_AZ:
                query = playersBox.query().order(PlayerModel_.name).build();
                break;
            case SORT_ZA:
                query = playersBox.query().order(PlayerModel_.name, QueryBuilder.DESCENDING).build();
                break;
            case SORT_GP:
                query = playersBox.query().order(PlayerModel_.gamesPlayed).build();
                break;
            default:
                query = playersBox.query().build();
                break;
        }
        rvAdapter.setPlayersList(query.find());
    }

    //returns true if playerIDs list does contain
    private boolean playerIDsContainID(long id) {
        for (int i = 0; i < playerIDs.size(); i++) {
            if (playerIDs.get(i).longValue() == id)
                return true;
        }
        return false;
    }

    private void playerIDsRemoveID(long id) {
        for (int i = 0; i < playerIDs.size(); i++) {
            if (playerIDs.get(i).longValue() == id)
                playerIDs.remove(i);
        }
    }
}