package isaacjschroeder.discotron.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.data.CourseModel;

public class CourseRVAdapter extends RecyclerView.Adapter {

    private List<CourseModel> courses;
    private final OnCourseClickListener listener;

    public CourseRVAdapter(List<CourseModel> courses, OnCourseClickListener listener) {
        this.courses = courses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CourseViewHolder cvh = (CourseViewHolder) holder;
        cvh.bind(courses.get(position), listener);
    }

    @Override
    public int getItemCount() { return courses.size(); }


    static class CourseViewHolder extends RecyclerView.ViewHolder {

        private TextView courseNameTV;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameTV = itemView.findViewById(R.id.rvah_course_name_tv);
        }

        public void bind(final CourseModel course, final CourseRVAdapter.OnCourseClickListener listener) {
            courseNameTV.setText(course.name);
            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCourseClick(itemView, getAdapterPosition());
                }
            });
        }
    }

    public interface OnCourseClickListener {
        void onCourseClick(View itemView, int position);
    }

    public void setCoursesList(List<CourseModel> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    public long getCourseId(int position) {
        return courses.get(position).id;
    }
}
