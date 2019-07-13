package pl.nieruchalski.scrumfamily.Fragments.SprintSummaryTaskBoard;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import pl.nieruchalski.scrumfamily.Adapters.CardViewMyTaskAdapter;
import pl.nieruchalski.scrumfamily.Adapters.CardViewTaskAdapter;
import pl.nieruchalski.scrumfamily.Fragments.SprintSummaryDataFragment;
import pl.nieruchalski.scrumfamily.Fragments.SprintSummaryFragment;
import pl.nieruchalski.scrumfamily.Fragments.TaskFragment;
import pl.nieruchalski.scrumfamily.Fragments.TasksBoardFragment;
import pl.nieruchalski.scrumfamily.HelpingClasses.RecyclerViewOnItemTouchListener;
import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.HelpingClasses.Task;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SprintSummaryTaskBoard extends Fragment {

    private ImageView home;
    private ImageView todo;
    private ImageView inprogress;
    private ImageView done;

    public ArrayList<Task> tasks;
    private RecyclerView tasksList;
    public int position = 0;

    public SprintSummaryTaskBoard() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).accFragment = "SprintSummaryTaskBoard";
        ((MainActivity) getActivity()).sprintSummaryTaskBoard = this;
        ((MainActivity) getActivity()).underlineOption();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        tasksList.setLayoutManager(layoutManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sprint_summary_task_board, container, false);

        if(tasks == null)
            tasks = new ArrayList<Task>();

        tasksList = (RecyclerView) view.findViewById(R.id.tasksList);

        home = (ImageView) view.findViewById(R.id.home);
        todo = (ImageView) view.findViewById(R.id.todo);
        inprogress = (ImageView) view.findViewById(R.id.inprogress);
        done = (ImageView) view.findViewById(R.id.done);

        tasksList.setAdapter(new CardViewTaskAdapter(tasks));

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blackIcons();
                home.setImageResource(R.mipmap.homegrey);
                tasksList.setAdapter(new CardViewTaskAdapter(tasks));
                getActivity().setTitle(getResources().getString(R.string.tasksBoard));
                position = 0;
            }
        });

        todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blackIcons();
                todo.setImageResource(R.mipmap.todogray);

                ArrayList<Task> tasksA = new ArrayList<Task>();

                for(int i=0; i<tasks.size(); i++){
                    if(tasks.get(i).getState() == 0)
                        tasksA.add(tasks.get(i));
                }

                tasksList.setAdapter(new CardViewTaskAdapter(tasksA));
                getActivity().setTitle(getResources().getString(R.string.tasksTodo));
                position = 1;
            }
        });

        inprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blackIcons();
                inprogress.setImageResource(R.mipmap.inprogressgrey);

                ArrayList<Task> tasksA = new ArrayList<Task>();

                for(int i=0; i<tasks.size(); i++){
                    if(tasks.get(i).getState() == 1)
                        tasksA.add(tasks.get(i));
                }

                tasksList.setAdapter(new CardViewTaskAdapter(tasksA));
                getActivity().setTitle(getResources().getString(R.string.tasksInProgress));
                position = 2;
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blackIcons();
                done.setImageResource(R.mipmap.done_gray);

                ArrayList<Task> tasksA = new ArrayList<Task>();

                for(int i=0; i<tasks.size(); i++){
                    if(tasks.get(i).getState() == 2)
                        tasksA.add(tasks.get(i));
                }

                tasksList.setAdapter(new CardViewTaskAdapter(tasksA));
                getActivity().setTitle(getResources().getString(R.string.tasksDone));
                position = 3;
            }
        });

        if(savedInstanceState !=null){

            position = savedInstanceState.getInt("position");

            ArrayList<String> titlesArray = savedInstanceState.getStringArrayList("titles");
            ArrayList<String> descriptionsArray = savedInstanceState.getStringArrayList("descriptions");
            ArrayList<String> statesArray = savedInstanceState.getStringArrayList("states");
            ArrayList<String> tookByArray = savedInstanceState.getStringArrayList("tookby");
            ArrayList<String> idArray = savedInstanceState.getStringArrayList("id");

            for(int i=0; i<titlesArray.size(); i++)
                tasks.add(new Task(idArray.get(i), titlesArray.get(i), descriptionsArray.get(i), statesArray.get(i), tookByArray.get(i)));

            switch (position){
                case 0:
                    home.callOnClick();
                    break;
                case 1:
                    todo.callOnClick();
                    break;
                case 2:
                    inprogress.callOnClick();
                    break;
                case 3:
                    done.callOnClick();
                    break;
            }
        }

        switch (position){
            case 0:
                home.callOnClick();
                break;
            case 1:
                todo.callOnClick();
                break;
            case 2:
                inprogress.callOnClick();
                break;
            case 3:
                done.callOnClick();
                break;
        }

        GestureDetectorCompat detector = new GestureDetectorCompat(getActivity(), new SprintSummaryTaskBoard.RecyclerViewOnGestureListener());

        RecyclerViewOnItemTouchListener tasksListOnItemTouch = new RecyclerViewOnItemTouchListener(detector);

        tasksList.addOnItemTouchListener(tasksListOnItemTouch);

        return view;
    }

    public void blackIcons()
    {
        home.setImageResource(R.mipmap.home);
        todo.setImageResource(R.mipmap.todo);
        inprogress.setImageResource(R.mipmap.inprogress);
        done.setImageResource(R.mipmap.done);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        outState.putInt("position", position);
    }

    public void onCardViewClicked(int position){
        SprintSummaryTask fragment = new SprintSummaryTask();
        CardViewTaskAdapter adapter = (CardViewTaskAdapter) tasksList.getAdapter();
        fragment.tasks = tasks;
        fragment.accPosition = this.position;
        fragment.task = adapter.getTasks().get(position);
        ((MainActivity)getActivity()).updateFragment(fragment, false);
    }

    public void onBackPressed() {
        ((MainActivity) getActivity()).updateFragment(new SprintSummaryFragment(), false);
    }

    private void onSwipeRight(){
        switch (position){
            case 0:
                break;
            case 1:
                home.callOnClick();
                break;
            case 2:
                todo.callOnClick();
                break;
            case 3:
                inprogress.callOnClick();
                break;
        }
    }

    private void onSwipeLeft(){
        switch (position){
            case 0:
                todo.callOnClick();
                break;
            case 1:
                inprogress.callOnClick();
                break;
            case 2:
                done.callOnClick();
                break;
            case 3:
                break;
        }
    }

    private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            View view = tasksList.findChildViewUnder(e.getX(), e.getY());
            int position = tasksList.getChildPosition(view);

            if(position >= 0)
                onCardViewClicked(position);


            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
}
