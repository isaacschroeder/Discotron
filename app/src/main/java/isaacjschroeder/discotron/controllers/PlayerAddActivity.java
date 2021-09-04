package isaacjschroeder.discotron.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.Query;
import isaacjschroeder.discotron.ObjectBox;
import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.data.PlayerModel;
import isaacjschroeder.discotron.data.PlayerModel_;

public class PlayerAddActivity extends AppCompatActivity {

    private EditText playerNameET;
    private Button finishBTN;
    private Box<PlayerModel> players;

    private String newName;
    private PlayerModel player;

    public static final int EDIT_PLAYER_RC = 1;
    public static final int ADD_PLAYER_RC = 2;
    private long editID; //for Recyclerview, not objectbox

    private static final int MAX_NAME_CHARACTERS = 16;

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_add);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        players = ObjectBox.get().boxFor(PlayerModel.class);

        playerNameET = findViewById(R.id.add_player_et);
        finishBTN = findViewById(R.id.add_player_finish_btn);

        Intent i = getIntent();
        editID = i.getLongExtra(ObjectBox.ID_EXTRA, ObjectBox.INVALID_ID);
        if (editID == ObjectBox.INVALID_ID) {

            setTitle("Create A New Player");

            finishBTN.setText("Create");

            finishBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //If name is not taken, add new player to db and return to previous activity
                    if (testPlayerFields()) {
                        player = new PlayerModel(newName);
                        players.put(player);
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            });
        } else {

            setTitle("Edit A Player");

            finishBTN.setText("Update");

            finishBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //If all good, add player to db
                    if (testPlayerFields()) {
                        player = players.get(editID);
                        player.name = newName;
                        players.put(player);
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            });
        }
    }

    //To ensure names are unique
    private boolean testPlayerFields() {
        newName = playerNameET.getText().toString();
        //Query for players with same name!
        if (!newName.isEmpty()) {
            if (newName.length() <= MAX_NAME_CHARACTERS) {
                Query<PlayerModel> query = players.query().equal(PlayerModel_.name, newName).build(); //Query all players with entered name
                List<PlayerModel> matchingNameList = query.find();
                if (matchingNameList.size() == 1 && editID == matchingNameList.get(0).id) { //if editing and same name is kept
                    return true;
                } else if (matchingNameList.isEmpty()) { //otherwise, no matching names allowed
                    return true;
                } else {
                    Toast.makeText(this, "There is already a player using that name fool!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Your player name can't exceed " + MAX_NAME_CHARACTERS + " characters fool!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "The players name can't be empty fool!", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}