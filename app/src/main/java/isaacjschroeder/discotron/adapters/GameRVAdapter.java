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
import isaacjschroeder.discotron.data.GameModel;

public class GameRVAdapter extends RecyclerView.Adapter {

    private List<GameModel> games;
    private final OnGameClickListener listener;

    public GameRVAdapter (List<GameModel> games, OnGameClickListener listener) {
        this.games = games;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_item, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GameViewHolder gvh = (GameViewHolder)holder;
        gvh.bind(games.get(position), listener);
    }

    @Override
    public int getItemCount() { return games.size(); }


    static class GameViewHolder extends RecyclerView.ViewHolder {

        private TextView gameNameTV;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameNameTV = itemView.findViewById(R.id.rvah_game_name_tv);
        }

        public void bind(final GameModel game, final GameRVAdapter.OnGameClickListener listener) {
            gameNameTV.setText(game.name);
            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onGameClick(itemView, getAdapterPosition());
                }
            });
        }
    }

    public interface OnGameClickListener {
        void onGameClick(View itemView, int position);
    }

    public void setGamesList(List<GameModel> games) {
        this.games = games;
        notifyDataSetChanged();
    }

    public long getGameID(int position) { return games.get(position).id; }
}
