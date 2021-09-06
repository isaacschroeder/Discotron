package isaacjschroeder.discotron.controllers;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import isaacjschroeder.discotron.ObjectBox;
import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.SharedPreferencesManager;
import isaacjschroeder.discotron.customviews.ScoreBoardView;
import isaacjschroeder.discotron.customviews.ScoreCardView;
import isaacjschroeder.discotron.data.GameModel;
import isaacjschroeder.discotron.data.ScoreCardModel;

public class GamePlayActivity extends AppCompatActivity {

    private TextView holeInfoTV;
    private TextView setScoreTV;
    private Button setScoreBTN;
    private ImageButton editMatchParBTN;
    private ImageButton holeSelectLeftBTN;
    private ImageButton holeSelectRightBTN;
    private ImageButton setScoreMinusBTN;
    private ImageButton setScorePlusBTN;
    private NumberPicker playerSelectNP;
    private HorizontalScrollView hscroll;
    private ScoreBoardView gameSBV;

    private GameModel game;

    private int currentHole;
    private int scoreSet;
    private int holeCount;
    private String playerNames[];

    private static final int MAX_SCORE = 100;
    private static final int STARTING_HOLE_NUM = 1;
    private static final float PLAYER_SELECTOR_TEXT_SIZE = 60.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get game id
        Intent i = getIntent();
        long id = i.getLongExtra(ObjectBox.ID_EXTRA, ObjectBox.INVALID_ID);

        //get the game
        game = ObjectBox.get().boxFor(GameModel.class).get(id);

        currentHole = STARTING_HOLE_NUM; //start on hole 1
        holeCount = game.course.getTarget().holes.size();

        setTitle("Playing " + game.name);

        //find views
        holeInfoTV = findViewById(R.id.game_play_holeinfo_tv);
        setScoreTV = findViewById(R.id.game_play_scoreset_tv);
        setScoreBTN = findViewById(R.id.game_play_scoreset_btn);
        editMatchParBTN = findViewById(R.id.game_play_editmatchpar_btn);
        holeSelectLeftBTN = findViewById(R.id.game_play_holeswitchleft_btn);
        holeSelectRightBTN = findViewById(R.id.game_play_holeswitchright_btn);
        setScoreMinusBTN = findViewById(R.id.game_play_scoreminus_btn);
        setScorePlusBTN = findViewById(R.id.game_play_scoreplus_btn);
        playerSelectNP = findViewById(R.id.game_play_playerselect_np);
        hscroll = findViewById(R.id.game_play_scoreboard_hscroll);

        gameSBV = new ScoreBoardView(this, game);
        hscroll.addView(gameSBV);

        //create array of player names and use to setup player name number picker
        int playerCount = game.scoreCards.size();
        playerNames = new String[playerCount];
        for (int x = 0; x < playerCount; x++) {
            playerNames[x] = game.scoreCards.get(x).player.getTarget().name;
        }
        playerSelectNP.setMinValue(0);
        playerSelectNP.setMaxValue(playerCount -1);
        playerSelectNP.setDisplayedValues(playerNames);
        playerSelectNP.setTextSize(PLAYER_SELECTOR_TEXT_SIZE);

        //Init textviews and scoreset
        updateHoleInfo();

        //CREATE CLICK LISTENERS
        setScoreBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long scoreCardID = game.getScoreCardIDFromPlayerName(playerNames[playerSelectNP.getValue()]);
                //update db
                game.setPlayerScore(scoreCardID, currentHole, scoreSet);
                //Missing put operations!

                //update scoreboard
                gameSBV.updateScoreEntry(scoreCardID, currentHole - 1, game.getMatchPar(currentHole).getPar(), scoreSet);
            }
        });
        editMatchParBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open dialog to edit match par
                AlertDialog.Builder builder = new AlertDialog.Builder(GamePlayActivity.this);
                builder.setMessage("Edit Par:");

                final NumberPicker numberPicker = new NumberPicker(GamePlayActivity.this);
                numberPicker.setMaxValue(CourseAddActivity.MAX_PAR_VALUE);
                numberPicker.setMinValue(1);
                numberPicker.setValue(game.getMatchPar(currentHole).getPar()); //start picker at current match par

                builder.setView(numberPicker);

                builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //apply match par change
                        int newMatchPar = numberPicker.getValue();
                        game.setMatchPar(currentHole, newMatchPar); //update db

                        //update scoreboard
                        gameSBV.updateMatchParEntry(currentHole - 1, game.course.getTarget().getHole(currentHole).getPar(), newMatchPar);
                        //need to update scores for hole in each player column also to have correct coloring
                        for (ScoreCardModel scoreCard : game.scoreCards) {
                            gameSBV.updateScoreEntry(scoreCard.id, currentHole - 1, newMatchPar, scoreCard.getScore(currentHole).getScore());
                        }

                        updateHoleInfo(); //update textviews
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        holeSelectLeftBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentHole == 1)
                    currentHole = holeCount;
                else
                    currentHole--;
                updateHoleInfo();
            }
        });
        holeSelectRightBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentHole == holeCount)
                    currentHole = 1;
                else
                    currentHole++;
                updateHoleInfo();
            }
        });
        setScoreMinusBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scoreSet > 1) {
                    scoreSet--;
                    setScoreTV.setText(String.valueOf(scoreSet));
                }
            }
        });
        setScorePlusBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scoreSet <= MAX_SCORE) {
                    scoreSet++;
                    setScoreTV.setText(String.valueOf(scoreSet));
                }
            }
        });

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

    private void updateHoleInfo() {
        int matchParForCurrentHole = game.getMatchPar(currentHole).getPar();
        holeInfoTV.setText("Hole " + currentHole + "\nPar " + matchParForCurrentHole);
        setScoreTV.setText(String.valueOf(matchParForCurrentHole));
        scoreSet = game.getMatchPar(currentHole).getPar(); //also update scoreset int
    }
}