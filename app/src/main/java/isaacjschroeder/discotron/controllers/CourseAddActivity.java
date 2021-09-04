package isaacjschroeder.discotron.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.Query;
import isaacjschroeder.discotron.ObjectBox;
import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.customviews.ScoreBoardView;
import isaacjschroeder.discotron.data.CourseModel;
import isaacjschroeder.discotron.data.CourseModel_;
import isaacjschroeder.discotron.data.HoleModel;
import isaacjschroeder.discotron.data.PlayerModel;
import isaacjschroeder.discotron.data.PlayerModel_;

import static java.lang.Integer.parseInt;

public class CourseAddActivity extends AppCompatActivity {

    private EditText courseNameET;
    private EditText courseHoleCountET;
    private Button finishBTN;
    private Button updateHoleParBTN;
    private Button setHoleCountBTN;
    private ScrollView vscroll;
    private NumberPicker holeSelectNP;
    private NumberPicker parSetNP;
    private TextView setDefaultParsTV;
    private TextView holeTV;
    private TextView parTV;

    private ScoreBoardView parPreviewSBV;

    private Box<CourseModel> courses;
    private CourseModel course;
    private String courseName;
    private int holeCount;
    //use rv to add holes? No, just number pickers for simplicity


    private static final int MAX_HOLE_COUNT = 100;
    private static final int MAX_PAR_VALUE = 10;
    private static final int INITIAL_HOLE_COUNT = 18;
    private static final int INITIAL_PAR_VALUE = 3;

    public static final int EDIT_COURSE_RC = 1;
    public static final int ADD_COURSE_RC = 2;
    private long editID; //for Recyclerview, not objectbox


    //NOTE: currently not implementing the ability to edit/change the holecount of a course after creation,
    //just delete course and make a new one. want to still allow changing of pars and name though

    //okay, having it so user only sets hole count once!

    //should setting hole count be a number picker instead??? could be DONE IN AN ALERT

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
        setContentView(R.layout.activity_course_add);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        courses = ObjectBox.get().boxFor(CourseModel.class);

        courseNameET = findViewById(R.id.add_course_name_et);
        courseHoleCountET = findViewById(R.id.add_course_holecount_et);
        finishBTN = findViewById(R.id.add_course_finish_btn);
        updateHoleParBTN = findViewById(R.id.add_course_updatepar_btn);
        setHoleCountBTN = findViewById(R.id.add_course_updateholecount_btn);
        vscroll = findViewById(R.id.add_course_vscroll);
        holeSelectNP = findViewById(R.id.add_course_hole_np);
        parSetNP = findViewById(R.id.add_course_par_np);
        setDefaultParsTV = findViewById(R.id.add_course_defaultpars_tv);
        holeTV = findViewById(R.id.add_course_holeselect_tv);
        parTV = findViewById(R.id.add_course_parset_tv);


        //set number picker bounds, some settings depend on whether editing or creating new course
        parSetNP.setMinValue(1);
        parSetNP.setMaxValue(MAX_PAR_VALUE);
        holeSelectNP.setMinValue(1);
        holeSelectNP.setValue(1);           //start hole picker at 1

        Intent i = getIntent();
        editID = i.getLongExtra(ObjectBox.ID_EXTRA, ObjectBox.INVALID_ID);
        if (editID == ObjectBox.INVALID_ID) {

            setTitle("Create A New Course");
            finishBTN.setText("Create");

            //set views that require a holecount to invisible
            vscroll.setVisibility(View.INVISIBLE);
            vscroll.setEnabled(false);
            finishBTN.setVisibility(View.INVISIBLE);
            finishBTN.setEnabled(false);
            updateHoleParBTN.setVisibility(View.INVISIBLE);
            updateHoleParBTN.setEnabled(false);
            parSetNP.setVisibility(View.INVISIBLE);
            parSetNP.setEnabled(false);
            holeSelectNP.setVisibility(View.INVISIBLE);
            holeTV.setEnabled(false);
            setDefaultParsTV.setVisibility(View.INVISIBLE);
            parTV.setVisibility(View.INVISIBLE);
            holeTV.setVisibility(View.INVISIBLE);


            //to edit course properties further, first need to set the holecount
            setHoleCountBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int hc = Integer.parseInt(courseHoleCountET.getText().toString());
                    if (hc <= MAX_HOLE_COUNT && hc > 0) {
                        holeCount = hc;
                        parSetNP.setValue(INITIAL_PAR_VALUE);                                           //start par picker at 3
                        courseHoleCountET.setText(String.valueOf(INITIAL_HOLE_COUNT));                  //start hole count setter at 18 holes
                        holeSelectNP.setMaxValue(hc);

                        //Show par editing items now that holecount is determined, DOES THIS HAVE ISSUES WITH LISTENERS? CAN STILL INTERACT WITH THESE
                        vscroll.setVisibility(View.VISIBLE);
                        vscroll.setEnabled(true);
                        finishBTN.setVisibility(View.VISIBLE);
                        finishBTN.setEnabled(true);
                        updateHoleParBTN.setVisibility(View.VISIBLE);
                        updateHoleParBTN.setEnabled(true);
                        parSetNP.setVisibility(View.VISIBLE);
                        parSetNP.setEnabled(true);
                        holeSelectNP.setVisibility(View.VISIBLE);
                        holeSelectNP.setEnabled(true);
                        setDefaultParsTV.setVisibility(View.VISIBLE);
                        parTV.setVisibility(View.VISIBLE);
                        holeTV.setVisibility(View.VISIBLE);

                        //prepare data and create par preview scoreboard based on holecount
                        course = new CourseModel();
                        for (int x = 1; x <= holeCount; x++)
                            course.holes.add(new HoleModel(x, INITIAL_PAR_VALUE));                           //start par of every hole at 3
                        parPreviewSBV = new ScoreBoardView(setHoleCountBTN.getContext(), course);
                        vscroll.addView(parPreviewSBV);

                        //disable editing of hole count
                        setHoleCountBTN.setEnabled(false);
                        courseHoleCountET.setEnabled(false); //change focusability and/or visibility too???
                    }
                    else {
                        Toast.makeText(setHoleCountBTN.getContext(), "Hole Count must be between 1 and 100 holes fool!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {

            setTitle("Edit A Course");
            finishBTN.setText("Update");

            //Retrieve course for editing and putting into db
            course = courses.get(editID);

            //disable hole count editing features
            setHoleCountBTN.setEnabled(false);
            courseHoleCountET.setText(String.valueOf(course.holes.size()));
            courseHoleCountET.setEnabled(false); //change focusability and/or visibility too???

            //set name ET
            courseNameET.setText(course.name);

            //setup pickers
            parSetNP.setValue(course.getHole(1).getPar());            //start par picker par of first hole
            holeSelectNP.setMaxValue(course.holes.size());

            parPreviewSBV = new ScoreBoardView(this, course);
            vscroll.addView(parPreviewSBV);
        }


        updateHoleParBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HoleModel h = course.getHole(holeSelectNP.getValue());

                //testdingo; does the modified hole need to be put? YES IT DOES, in addition to the course itself!!!
                h.setPar(parSetNP.getValue());
                ObjectBox.get().boxFor(HoleModel.class).put(h);

                parPreviewSBV.updateScoreEntry(0, holeSelectNP.getValue() - 1, parSetNP.getValue());
            }
        });

        finishBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if no issues with new course entry, add to db and return to previous activity
                if (testCourseFields()) {
                    course.name = courseName;
                    courses.put(course);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

    }

    //to ensure names are unique
    private boolean testCourseFields() {
        courseName = courseNameET.getText().toString();
        //Query for players with same name!
        if (!courseName.isEmpty()) {
            Query<CourseModel> query = courses.query().equal(CourseModel_.name, courseName).build(); //Query all courses with entered name
            List<CourseModel> matchingNameList = query.find();
            if (matchingNameList.size() == 1 && editID == matchingNameList.get(0).id) { //if editing and same name is kept
                return true;
            } else if (matchingNameList.isEmpty()) { //otherwise, no matching names allowed
                return true;
            } else {
                Toast.makeText(this, "There is already a course using that name fool!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "The course's name can't be empty fool!", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}