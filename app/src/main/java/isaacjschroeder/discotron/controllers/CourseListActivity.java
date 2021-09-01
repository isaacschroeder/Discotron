package isaacjschroeder.discotron.controllers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import isaacjschroeder.discotron.ObjectBox;
import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.adapters.CourseRVAdapter;
import isaacjschroeder.discotron.data.CourseModel;
import isaacjschroeder.discotron.data.CourseModel_;
import isaacjschroeder.discotron.data.HoleModel;

public class CourseListActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView rv;
    private CourseRVAdapter rvAdapter;
    private Box<CourseModel> courseBox;

    private static final int SORT_AZ = 0;
    private static final int SORT_ZA = 1;
    private static final int SORT_GP = 2;
    private int sortingMethod = SORT_AZ;

    //for determining purpose of launching this activity to behave accordingly.
    //is passed in from calling activity as an int extra
    public static final String COURSE_LIST_USE_EXTRA = "COURSE_LIST_USE_EXTRA";
    public static final int COURSE_LIST_USE_VIEW_DETAILS = 0;
    public static final int COURSE_LIST_USE_SELECT_AND_RETURN_ID = 1;

    public static final String COURSE_LIST_COURSE_ID_EXTRA = "COURSE_LIST_COURSE_ID_EXTRA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        //Get box for courses
        courseBox = ObjectBox.get().boxFor(CourseModel.class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Fab add course button
        fab = findViewById(R.id.course_list_add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch add new course activity
                Intent i = new Intent(CourseListActivity.this, CourseAddActivity.class);
                i.putExtra(ObjectBox.ID_EXTRA, ObjectBox.INVALID_ID);
                startActivityForResult(i, CourseAddActivity.ADD_COURSE_RC);
            }
        });

        //Setup RecyclerView
        rv = findViewById(R.id.course_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(CourseListActivity.this));

        //Determine list use and setup click listeners and title accordingly
        Intent intent = getIntent();
        int listUse = intent.getIntExtra(COURSE_LIST_USE_EXTRA, COURSE_LIST_USE_SELECT_AND_RETURN_ID);
        if (listUse == COURSE_LIST_USE_VIEW_DETAILS) {
            setTitle("Courses");
            rvAdapter = new CourseRVAdapter(courseBox.getAll(), new CourseRVAdapter.OnCourseClickListener() {
                @Override
                public void onCourseClick(View itemView, int position) {
                    //Launch view course details activity
                    Intent i = new Intent(CourseListActivity.this, CourseDetailsActivity.class);
                    i.putExtra(ObjectBox.ID_EXTRA, rvAdapter.getCourseId(position));
                    startActivityForResult(i, CourseAddActivity.EDIT_COURSE_RC);
                }
            });
        }
        else {
            setTitle("Select a Course");
            rvAdapter = new CourseRVAdapter(courseBox.getAll(), new CourseRVAdapter.OnCourseClickListener() {
                @Override
                public void onCourseClick(View itemView, int position) {
                    //Finish activity, returning the id for the course to be used
                    Intent passBackId = new Intent();
                    passBackId.putExtra(COURSE_LIST_COURSE_ID_EXTRA, rvAdapter.getCourseId(position));
                    setResult(RESULT_OK, passBackId);
                    finish();
                }
            });
        }

        rv.setAdapter(rvAdapter);

        //Initially sort by games played???
        updateSortedList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_course_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_course_list_action_sort_az:
                sortingMethod = SORT_AZ;
                break;
            case R.id.menu_course_list_action_sort_za:
                sortingMethod = SORT_ZA;
                break;
            case R.id.menu_course_list_action_sort_gp:
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            updateSortedList();
        }
    }

    private void updateSortedList() {
        Query<CourseModel> query;
        switch (sortingMethod) {
            case SORT_AZ:
                query = courseBox.query().order(CourseModel_.name).build();
                break;
            case SORT_ZA:
                query = courseBox.query().order(CourseModel_.name, QueryBuilder.DESCENDING).build();
                break;
            case SORT_GP:
                query = courseBox.query().order(CourseModel_.gamesPlayed).build();
                break;
            default:
                query = courseBox.query().build();
                break;
        }
        rvAdapter.setCoursesList(query.find());
    }
}