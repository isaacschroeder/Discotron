package isaacjschroeder.discotron.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.data.PlayerModel;

public class  PlayerRVAdapter extends RecyclerView.Adapter {

    private List<PlayerModel> players;
    private final OnPlayerClickListener listener;

    public PlayerRVAdapter(List<PlayerModel> players, OnPlayerClickListener listener) {
        this.players = players;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_item, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PlayerViewHolder pvh = (PlayerViewHolder) holder;
        pvh.bind(players.get(position), listener);
    }

    @Override
    public int getItemCount() {
         return players.size();
    }


    static class PlayerViewHolder extends RecyclerView.ViewHolder {

        private TextView playerNameTV;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNameTV = itemView.findViewById(R.id.rvah_player_name_tv);
        }

        public void bind(final PlayerModel player, final OnPlayerClickListener listener) {
            playerNameTV.setText(player.name);
            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPlayerClick(itemView, getAdapterPosition());
                }
            });
        }
    }

    public interface OnPlayerClickListener {
        void onPlayerClick(View itemView, int position);
    }

    public void setPlayersList(List<PlayerModel> players) {
        this.players = players;
        //update because data changed here!
        notifyDataSetChanged(); //(most general notifier)
    }

    public long getPlayerId(int position) {
        return players.get(position).id;
    }
}
