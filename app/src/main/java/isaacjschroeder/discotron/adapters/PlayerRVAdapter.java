package isaacjschroeder.discotron.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import isaacjschroeder.discotron.R;
import isaacjschroeder.discotron.data.PlayerModel;

public class  PlayerRVAdapter extends RecyclerView.Adapter {

    private List<PlayerModel> players;
    private final OnPlayerClickListener listener;

    private Context context; //FOR SELECT FUNCTIONALITY
    private List<Long> selectedPlayers; //FOR SELECT FUNCTIONALITY

    public PlayerRVAdapter(Context context, List<PlayerModel> players, OnPlayerClickListener listener) {
        this.context = context; //FOR SELECT FUNCTIONALITY
        this.players = players;
        this.listener = listener;
        selectedPlayers = new ArrayList<Long>(); //FOR SELECT FUNCTIONALITY
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
        //FOR SELECT FUNCTIONALITY
        //provide color based on whether selected or not
        int color;
        if (isPlayerSelected(players.get(position).id))
            color = context.getResources().getColor(R.color.list_selected);
        else
            color = context.getResources().getColor(R.color.list_notselected);
        //FOR SELECT FUNCTIONALITY
        pvh.bind(players.get(position), listener, color);
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

        public void bind(final PlayerModel player, final OnPlayerClickListener listener, int color) {
            playerNameTV.setText(player.name);
            itemView.setBackgroundColor(color);
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

    //FOR SELECT FUNCTIONALITY
    public void setPlayersSelectedList(List<Long> selected) {
        selectedPlayers = selected;
    }

    private boolean isPlayerSelected(long id) {
        for (int i = 0; i < selectedPlayers.size(); i++) {
            if (id == selectedPlayers.get(i).longValue())
                return true;
        }
        return false;
    }
    //FOR SELECT FUNCTIONALITY
}
