package isaacjschroeder.discotron.controllers;


import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.NumberPicker;

import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.customviews.ScoreBoardView;

public class GamePlayActivity extends AppCompatActivity {

    private Button enterScoreBTN;
    private NumberPicker holeSelectNP;
    private HorizontalScrollView hscroll;
    private ScoreBoardView gameSBV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);




    }
}