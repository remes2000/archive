package pl.nieruchalski.scrumfamily.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.HelpingClasses.Task;
import pl.nieruchalski.scrumfamily.R;

/**
 * Created by michal on 04.03.17.
 */

public class CardViewMyTaskAdapter extends RecyclerView.Adapter<CardViewMyTaskAdapter.ViewHolder> {

    private ArrayList<Task> tasks;
    private ArrayList<Sprint> sprints;
    private Context context;

    public ArrayList<Task> getTasks(){
        return tasks;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    public CardViewMyTaskAdapter(ArrayList<Task> tasks, ArrayList<Sprint> sprints,Context context){
        this.tasks = tasks;
        this.sprints = sprints;
        this.context = context;
    }

    @Override
    public CardViewMyTaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.mytask_card_view, parent, false);
        return new CardViewMyTaskAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(CardViewMyTaskAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        TextView title = (TextView) cardView.findViewById(R.id.myTaskTitle);
        TextView description = (TextView) cardView.findViewById(R.id.myTaskDescription);
        TextView state = (TextView)  cardView.findViewById(R.id.myTaskStatus);
        TextView sprintTitle = (TextView)  cardView.findViewById(R.id.myTaskSprintTitle);

        title.setText(tasks.get(position).getTitle());

        sprintTitle.setText(titleToUseless(sprints.get(position).getTitle()));

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
            case 1:
                state.setText(cardView.getResources().getString(R.string.inprogress));
                cardView.setBackgroundColor(Color.rgb(188, 229, 255));
                break;
            case 2:
                state.setText(cardView.getResources().getString(R.string.done));
                cardView.setBackgroundColor(Color.rgb(188, 255, 190));
                break;
        }

    }

    private String titleToUseless(String title)
    {
        int l = title.length();
        StringBuilder builder = new StringBuilder(title);
        for(int i=0; i<l; i++)
        {
            if(builder.charAt(i) == '_')
                builder.setCharAt(i, ' ');
        }
        return builder.toString();
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
