package pl.nieruchalski.scrumfamily.Adapters;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import pl.nieruchalski.scrumfamily.HelpingClasses.Task;
import pl.nieruchalski.scrumfamily.R;

/**
 * Created by michal on 04.03.17.
 */

public class CardViewTaskAdapter extends RecyclerView.Adapter<CardViewTaskAdapter.ViewHolder> {

    private ArrayList<Task> tasks;

    public ArrayList<Task> getTasks(){
        return tasks;
    }

    public static interface Listener{
        public void onClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    public CardViewTaskAdapter(ArrayList<Task> tasks){
        this.tasks = tasks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_view, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        TextView title = (TextView) cardView.findViewById(R.id.titletask);
        TextView description = (TextView) cardView.findViewById(R.id.descriptiontask);
        TextView state = (TextView)  cardView.findViewById(R.id.status);
        TextView usernick = (TextView)  cardView.findViewById(R.id.usernick);

        title.setText(tasks.get(position).getTitle());
        description.setText(tasks.get(position).getDescription());

        String descriptionContent = tasks.get(position).getDescription();
        String cuttedDescription = "";

        if(descriptionContent.length() > 256)
        {
            for(int i=0; i<256; i++)
                cuttedDescription = cuttedDescription + descriptionContent.charAt(i);

            cuttedDescription = cuttedDescription + "...";

            description.setText(cuttedDescription);
        }

        else
            description.setText(descriptionContent);

        switch (tasks.get(position).getState())
        {
            case 0:
                state.setText(cardView.getResources().getString(R.string.todo));
                usernick.setText("");
                cardView.setBackgroundColor(Color.rgb(255, 196, 206));
                break;
            case 1:
                state.setText(cardView.getResources().getString(R.string.inprogress));
                usernick.setText(tasks.get(position).getTookBy());
                cardView.setBackgroundColor(Color.rgb(188, 229, 255));
                break;
            case 2:
                state.setText(cardView.getResources().getString(R.string.done));
                usernick.setText(tasks.get(position).getTookBy());
                cardView.setBackgroundColor(Color.rgb(188, 255, 190));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
