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
import android.widget.TextView;

import isaacjschroeder.discotron.ObjectBox;
import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.data.PlayerModel;

public class PlayerDetailsActivity extends AppCompatActivity {

    private TextView nameTV;
    private TextView gamesPlayedTV;

    private long id;
    private int editResult = RESULT_CANCELED;
    private PlayerModel p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_details);

        setTitle("Player Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        id = i.getLongExtra(ObjectBox.ID_EXTRA, ObjectBox.INVALID_ID);

        p = ObjectBox.get().boxFor(PlayerModel.class).get(id);

        nameTV = findViewById(R.id.player_detials_name_tv);
        gamesPlayedTV = findViewById(R.id.player_detials_gamecount_tv);
        nameTV.setText("Name: " + p.name);
        gamesPlayedTV.setText("Games Played: " + p.gamesPlayed);
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
                Intent i = new Intent(PlayerDetailsActivity.this, PlayerAddActivity.class);
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

    //pass result code for calling activity (playerlistactivity) to determine if item was edited
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            p = ObjectBox.get().boxFor(PlayerModel.class).get(id);
            nameTV.setText("Name: " + p.name);
        }
        editResult = resultCode;
    }
}