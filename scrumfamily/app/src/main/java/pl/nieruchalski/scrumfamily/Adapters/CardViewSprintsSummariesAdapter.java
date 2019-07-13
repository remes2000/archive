package pl.nieruchalski.scrumfamily.Adapters;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

import pl.nieruchalski.scrumfamily.Fragments.SprintSummaryFragment;
import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.R;

/**
 * Created by michal on 10.03.17.
 */

public class CardViewSprintsSummariesAdapter extends RecyclerView.Adapter<CardViewSprintsSummariesAdapter.ViewHolder> {

    private ArrayList<Sprint> sprints;
    private ArrayList<String> startDates;
    private ArrayList<String> endDates;
    private ArrayList<Boolean> saved;
    private Context context;
    private SprintSummaryFragment sprintSummaryFragment;
    private ArrayList<CardView> cardViews;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    public CardViewSprintsSummariesAdapter(ArrayList<Sprint> sprints, ArrayList<String> startDates, ArrayList<String> endDates, ArrayList<Boolean> saved, Context context, SprintSummaryFragment sprintSummaryFragment){
        this.sprints = sprints;
        this.startDates = startDates;
        this.endDates = endDates;
        this.saved = saved;
        this.context = context;
        this.sprintSummaryFragment = sprintSummaryFragment;
        cardViews = new ArrayList<CardView>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CardView cv = null;

        switch (viewType) {
            case 0:
            cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.sprintssummary_card_view, parent, false);
                break;
            case 1:
            cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.alternative_sprintsummary_card_view, parent, false);
                break;
        }
        return new ViewHolder(cv);
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        if(saved.get(position))
            return 1;
        else
            return 0;
    }

    public void hideProgressBar(int position){
        ((ProgressBar) cardViews.get(position).findViewById(R.id.onSaveProgressBar)).setVisibility(View.INVISIBLE);
        ((ImageView) cardViews.get(position).findViewById(R.id.saved)).setVisibility(View.VISIBLE);
    }

    public void showProgressBar(int position){
        ((ProgressBar) cardViews.get(position).findViewById(R.id.onSaveProgressBar)).setVisibility(View.VISIBLE);
    }

    public void changeToNormalView(int position){
        ((ProgressBar) cardViews.get(position).findViewById(R.id.onSaveProgressBar)).setVisibility(View.INVISIBLE);
        ((ImageView) cardViews.get(position).findViewById(R.id.saved)).setVisibility(View.INVISIBLE);
        ((Button) cardViews.get(position).findViewById(R.id.saveButton)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CardView cardView = holder.cardView;

        final TextView sprintTitle = (TextView) cardView.findViewById(R.id.summarySprintTitle);
        TextView startDate = (TextView) cardView.findViewById(R.id.startDate);
        TextView endDate = (TextView) cardView.findViewById(R.id.endDate);

        TextView timeInDays;
        TextView timeText;
        final Button saveButton;
        final ProgressBar progressBar;

        if(getItemViewType(position) == 0) {
            timeInDays = (TextView) cardView.findViewById(R.id.time);
            timeText = (TextView) cardView.findViewById(R.id.canDownloadFor);

            saveButton = (Button) cardView.findViewById(R.id.saveButton);

            Calendar todayIscalendar = Calendar.getInstance();

            Calendar deleteCalendar = Calendar.getInstance();

            Calendar sprintEndCalendar = Calendar.getInstance();

            String[] yearmonthday = endDates.get(position).split("-");

            sprintEndCalendar.set(Integer.parseInt(yearmonthday[0]), Integer.parseInt(yearmonthday[1]) - 1, Integer.parseInt(yearmonthday[2]));

            deleteCalendar.set(sprintEndCalendar.get(Calendar.YEAR), sprintEndCalendar.get(Calendar.MONTH), sprintEndCalendar.get(Calendar.DAY_OF_MONTH));
            deleteCalendar.add(Calendar.DAY_OF_MONTH, 7);


            Log.d("TODAYCALENDARREMES", Integer.toString(todayIscalendar.get(Calendar.YEAR)));
            Log.d("TODAYCALENDARREMES", Integer.toString(todayIscalendar.get(Calendar.MONTH)));
            Log.d("TODAYCALENDARREMES", Integer.toString(todayIscalendar.get(Calendar.DAY_OF_MONTH)));

            Log.d("ENDDATEREMES", Integer.toString(sprintEndCalendar.get(Calendar.YEAR)));
            Log.d("ENDDATEREMES", Integer.toString(sprintEndCalendar.get(Calendar.MONTH)));
            Log.d("ENDDATEREMES", Integer.toString(sprintEndCalendar.get(Calendar.DAY_OF_MONTH)));

            Log.d("DELETEDATEREMES", Integer.toString(deleteCalendar.get(Calendar.YEAR)));
            Log.d("DELETEDATEREMES", Integer.toString(deleteCalendar.get(Calendar.MONTH)));
            Log.d("DELETEDATEREMES", Integer.toString(deleteCalendar.get(Calendar.DAY_OF_MONTH)));

            long daysinmillis = (deleteCalendar.getTimeInMillis()) - todayIscalendar.getTimeInMillis();

            int days = (int) daysinmillis / 86400000;

            if(days == 0)
                timeText.setText(context.getResources().getString(R.string.youCanSaveOnlyToday));
            else
                timeInDays.setText(" " + Integer.toString(days) + " " +context.getResources().getString(R.string.days));

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sprintSummaryFragment.onSaveButtonClicked(position);
                    saveButton.setVisibility(View.INVISIBLE);
                }
            });

        }


        sprintTitle.setText(titleToUseless(sprints.get(position).getTitle()));
        startDate.setText(startDates.get(position));
        endDate.setText(endDates.get(position));

        View.OnClickListener listener = null;

        if(getItemViewType(position) == 0){
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sprintSummaryFragment.onCardViewClicked(position);
                }
            };
        }
        else{
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sprintSummaryFragment.onSavedCardViewClicked(position);
                }
            };
        }

        cardView.setOnClickListener(listener);

        cardViews.add(cardView);

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

    public int getItemCount(){return sprints.size();}
}
