package pl.nieruchalski.scrumfamily.Fragments.SprintSummaryTaskBoard;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.HelpingClasses.Task;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SprintSummaryTask extends Fragment {

    private TextView title;
    private TextView description;
    private TextView state;
    private TextView claimedByText;
    private TextView claimedBy;
    private ImageView imageView;
    private TextView sprintTitleTV;

    public ArrayList<Task> tasks;
    public Task task;
    public int accPosition;

    public SprintSummaryTask() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).accFragment = "SprintSummaryTask";
        ((MainActivity) getActivity()).sprintSummaryTask = this;
        ((MainActivity) getActivity()).underlineOption();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sprint_summary_task, container, false);

        if(savedInstanceState != null){

            tasks = new ArrayList<Task>();

            ArrayList<String> titlesArray = savedInstanceState.getStringArrayList("titles");
            ArrayList<String> descriptionsArray = savedInstanceState.getStringArrayList("descriptions");
            ArrayList<String> statesArray = savedInstanceState.getStringArrayList("states");
            ArrayList<String> tookByArray = savedInstanceState.getStringArrayList("tookby");
            ArrayList<String> idArray = savedInstanceState.getStringArrayList("id");

            for(int i=0; i<titlesArray.size(); i++)
                tasks.add(new Task(idArray.get(i), titlesArray.get(i), descriptionsArray.get(i), statesArray.get(i), tookByArray.get(i)));

            task = new Task(savedInstanceState.getString("taskId"), savedInstanceState.getString("title"),
                    savedInstanceState.getString("description"), savedInstanceState.getString("state"),
                    savedInstanceState.getString("tookBy"));

            accPosition = savedInstanceState.getInt("accPosition");
        }

        title = (TextView) view.findViewById(R.id.taskTitle);
        description = (TextView) view.findViewById(R.id.taskDescription);
        state = (TextView) view.findViewById(R.id.state);
        claimedByText = (TextView) view.findViewById(R.id.claimedByText);
        claimedBy = (TextView) view.findViewById(R.id.claimedBy);
        sprintTitleTV = (TextView) view.findViewById(R.id.sprintTitle);
        imageView = (ImageView) view.findViewById(R.id.imageViewState);

        title.setText(task.getTitle());
        description.setText(task.getDescription());;

        switch (task.getState())
        {
            case 0:
                state.setText(getResources().getString(R.string.todo));
                claimedByText.setText(getResources().getString(R.string.taskisstealtodo));
                imageView.setImageResource(R.mipmap.todo);
                view.setBackgroundColor(Color.rgb(255, 196, 206));
                break;
            case 1:
                state.setText(getResources().getString(R.string.inprogress));
                claimedByText.setText(getResources().getString(R.string.doingTask));
                claimedBy.setText(task.getTookBy());
                imageView.setImageResource(R.mipmap.inprogress);
                view.setBackgroundColor(Color.rgb(188, 229, 255));
                break;
            case 2:
                state.setText(getResources().getString(R.string.done));
                claimedByText.setText(getResources().getString(R.string.doneTask));
                claimedBy.setText(task.getTookBy());
                imageView.setImageResource(R.mipmap.done);
                view.setBackgroundColor(Color.rgb(188, 255, 190));
                break;
        }

        return view;

    }

    public void onBackPressed(){
        SprintSummaryTaskBoard fragment = new SprintSummaryTaskBoard();
        fragment.tasks = tasks;
        fragment.position = accPosition;
        ((MainActivity) getActivity()).updateFragment(fragment, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("taskId", Integer.toString(task.getId()));
        outState.putString("tookBy", task.getTookBy());
        outState.putString("state", Integer.toString(task.getState()));
        outState.putString("title", task.getTitle());
        outState.putString("description", task.getDescription());

        ArrayList<String> titlesArray = new ArrayList<String>();
        ArrayList<String> descriptionsArray = new ArrayList<String>();
        ArrayList<String> statesArray = new ArrayList<String>();
        ArrayList<String> tookByArray = new ArrayList<String>();
        ArrayList<String> idArray = new ArrayList<String>();

        for(int i=0; i<tasks.size(); i++)
        {
            titlesArray.add(tasks.get(i).getTitle());
            descriptionsArray.add(tasks.get(i).getDescription());
            statesArray.add(Integer.toString(tasks.get(i).getState()));
            tookByArray.add(tasks.get(i).getTookBy());
            idArray.add(Integer.toString(tasks.get(i).getId()));
        }

        outState.putStringArrayList("titles", titlesArray);
        outState.putStringArrayList("descriptions", descriptionsArray);
        outState.putStringArrayList("states", statesArray);
        outState.putStringArrayList("tookby", tookByArray);
        outState.putStringArrayList("id", idArray);

        outState.putInt("accPosition", accPosition);
    }
}
