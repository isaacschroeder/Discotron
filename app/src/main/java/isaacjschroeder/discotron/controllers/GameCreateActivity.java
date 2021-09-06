package isaacjschroeder.discotron.controllers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.Query;
import isaacjschroeder.discotron.ObjectBox;
import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.SharedPreferencesManager;
import isaacjschroeder.discotron.data.CourseModel;
import isaacjschroeder.discotron.data.CourseModel_;
import isaacjschroeder.discotron.data.GameModel;
import isaacjschroeder.discotron.data.GameModel_;
import isaacjschroeder.discotron.data.MatchParModel;
import isaacjschroeder.discotron.data.PlayerModel;
import isaacjschroeder.discotron.data.ScoreCardModel;
import isaacjschroeder.discotron.data.ScoreModel;

public class GameCreateActivity extends AppCompatActivity {

    private TextView courseNameTV;
    private TextView playerNamesTV;
    private EditText gameNameET;
    private Button chooseCourseBTN;
    private Button choosePlayersBTN;
    private Button createGameBTN;

    private String gameName;

    private static final int COURSE_ID_RC = 1;
    private static final int PLAYER_IDS_RC = 2;

    private static final int MAX_NAME_CHARACTERS = 16;

    private CourseModel course;
    private List<PlayerModel> players;

    private Box<GameModel> games;
    private GameModel game;


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
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_create);

        setTitle("New Game Setup");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        games = ObjectBox.get().boxFor(GameModel.class);

        courseNameTV = findViewById(R.id.game_create_coursename_tv);
        playerNamesTV = findViewById(R.id.game_create_playernames_tv);
        chooseCourseBTN = findViewById(R.id.game_create_selectcourse_btn);
        choosePlayersBTN = findViewById(R.id.game_create_selectplayers_btn);
        createGameBTN = findViewById(R.id.game_create_finish_btn);
        gameNameET = findViewById(R.id.game_create_gamename_et);

        chooseCourseBTN.setText("Select");
        choosePlayersBTN.setText("Select");

        chooseCourseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch course list activity with use set to selecting and returning ID
                Intent i = new Intent(GameCreateActivity.this, CourseListActivity.class);
                i.putExtra(CourseListActivity.COURSE_LIST_USE_EXTRA, CourseListActivity.COURSE_LIST_USE_SELECT_AND_RETURN_ID);
                startActivityForResult(i, COURSE_ID_RC);
            }
        });

        choosePlayersBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GameCreateActivity.this, PlayersListActivity.class);
                i.putExtra(PlayersListActivity.PLAYER_LIST_USE_EXTRA, PlayersListActivity.PLAYER_LIST_USE_SELECT_AND_RETURN_MULTIPLE_IDS);
                startActivityForResult(i, PLAYER_IDS_RC);
            }
        });

        createGameBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (testGameFields()) { //if game name checks out
                    //create game with provided course and players, launch play game activity, passing the newly created game id
                    game = new GameModel();
                    game.name = gameName;               //Set name
                    game.course.setTarget(course);      //set course
                    for (int i = 0; i < players.size(); i++) {
                        //Create a scorecard for each player and add them to the game
                        ScoreCardModel scoreCard = new ScoreCardModel();
                        scoreCard.player.setTarget(players.get(i));     //set the scorecards player
                        players.get(i).cards.add(scoreCard);            //set the players scorecard - does this need to be done on both sides of the relation?
                        scoreCard.game.setTarget(game);                 //set scorecards game

                        for (int j = 0; j < course.holes.size(); j++) {                             //Do scores need to be added yet or can they be created and added when the score is changed?
                            //Create scores for scorecard
                            ScoreModel score = new ScoreModel(-1, j + 1);   //default score of -1 for error catching
                            score.scoreCard.setTarget(scoreCard);                            //set the scores scorecard     ****************IS THIS UNNECESSARY??????? done multiple times in this section
                            scoreCard.scores.add(score);                                     //set the scorecards score

                            //Create match par data, set it to default pars from course for now
                            MatchParModel matchPar = new MatchParModel(j + 1, course.getHole(j + 1).getPar());
                            matchPar.game.setTarget(game);                                   //set the matchPars game
                            game.matchPars.add(matchPar);                                    //set the games matchPar
                        }
                        game.scoreCards.add(scoreCard);
                    }

                    games.put(game);        //finally, put the game in the db - does this add everything else? I guess it does!

                    //Need to launch game from main menu, so return the game id and shiz to the main menu
                    SharedPreferencesManager.write(SharedPreferencesManager.GAME_IN_PROGRESS_ID, game.id);
                    Intent passBackID = new Intent();
                    passBackID.putExtra(ObjectBox.ID_EXTRA, game.id);
                    setResult(RESULT_OK, passBackID);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //set selected course
        if (requestCode == COURSE_ID_RC && resultCode == RESULT_OK) {
            long id = data.getLongExtra(CourseListActivity.COURSE_LIST_COURSE_ID_EXTRA, ObjectBox.INVALID_ID);
            if (id != ObjectBox.INVALID_ID) {
                course = ObjectBox.get().boxFor(CourseModel.class).get(id);
                //update name on screen
                courseNameTV.setText("Course: " + course.name);
                chooseCourseBTN.setText("Change");
            }
        }
        //set selected players
        else if (requestCode == PLAYER_IDS_RC && resultCode == RESULT_OK) {
            playerNamesTV.setText("Players:");
            long[] ids = data.getLongArrayExtra(PlayersListActivity.PLAYER_LIST_PLAYER_MULTIPLE_IDS_EXTRA);
            players = new ArrayList<>();
            for (long id : ids) {
                PlayerModel p = ObjectBox.get().boxFor(PlayerModel.class).get(id);
                players.add(p);
                //playerNamesTV.setText(playerNamesTV.getText().toString() + " " + p.name);
            }
            playerNamesTV.setText("Players: " + players.size()); //for now just mention number of players
        }
    }

    //to ensure names are unique
    private boolean testGameFields() {
        gameName = gameNameET.getText().toString();
        //Query for players with same name!
        if (!gameName.isEmpty()) {
            if (gameName.length() <= MAX_NAME_CHARACTERS) {
                Query<GameModel> query = games.query().equal(GameModel_.name, gameName).build(); //Query all courses with entered name
                List<GameModel> matchingNameList = query.find();
                if (matchingNameList.isEmpty()) { //otherwise, no matching names allowed
                    if (course != null) {
                        if (players != null && !players.isEmpty()) {
                            return true;
                        } else {
                            Toast.makeText(this, "You have to add players to your game fool!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "You have choose a course for your game fool!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "There is already a game using that name fool!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Your game name can't exceed " + MAX_NAME_CHARACTERS + " characters fool!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "The game's name can't be empty fool!", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}