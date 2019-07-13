package pl.nieruchalski.scrumfamily.Adapters;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pl.nieruchalski.scrumfamily.Fragments.OrganiseSprintsFragment;
import pl.nieruchalski.scrumfamily.R;

/**
 * Created by michal on 11.03.17.
 */

public class CardViewOrgainseSprintsAdapter extends RecyclerView.Adapter<CardViewOrgainseSprintsAdapter.ViewHolder>{

    private ArrayList<String> options;
    private ArrayList<Integer> imageResources;
    private OrganiseSprintsFragment organiseSprintsFragment;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    public CardViewOrgainseSprintsAdapter(ArrayList<String> options, ArrayList<Integer> imageResources, OrganiseSprintsFragment organiseSprintsFragment){
        this.options = options;
        this.imageResources = imageResources;
        this.organiseSprintsFragment = organiseSprintsFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.organise_sprints_cardview, parent, false);
        return new CardViewOrgainseSprintsAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CardView cardView = holder.cardView;

        TextView title = (TextView) cardView.findViewById(R.id.optionText);
        ImageView icon = (ImageView) cardView.findViewById(R.id.optionIcon);

        title.setText(options.get(position));
        icon.setImageResource(imageResources.get(position));

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                organiseSprintsFragment.onOptionClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }
}
