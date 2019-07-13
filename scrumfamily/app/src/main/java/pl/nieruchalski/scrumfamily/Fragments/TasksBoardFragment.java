package pl.nieruchalski.scrumfamily.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import pl.nieruchalski.scrumfamily.Adapters.CardViewTaskAdapter;
import pl.nieruchalski.scrumfamily.HelpingClasses.RecyclerViewOnItemTouchListener;
import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.HelpingClasses.Task;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TasksBoardFragment extends Fragment{

    public TasksBoardFragment() {
        // Required empty public constructor
    }

    private int choice;

    public int accFilter = -1;

    public Task scrollToTask = null;

    public Sprint sprint;
    private ArrayList<Task> tasks;

    private RecyclerView tasksList;

    private ArrayList<Sprint> sprints;

    public Boolean loaded_flag = false;
    public Boolean refresh_flag = false;

    private ImageView home;
    private ImageView todo;
    private ImageView inprogress;
    private ImageView done;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks_board, container, false);
        getActivity().setTitle(getResources().getString(R.string.tasksBoard));
        sprints = new ArrayList<Sprint>();
        tasks = new ArrayList<Task>();
        tasksList = (RecyclerView) view.findViewById(R.id.tasksList);
        if(savedInstanceState!=null) {
            loaded_flag = savedInstanceState.getBoolean("flag");
            ArrayList<String> titlesArray = savedInstanceState.getStringArrayList("titles");
            ArrayList<String> descriptionsArray = savedInstanceState.getStringArrayList("descriptions");
            ArrayList<String> statesArray = savedInstanceState.getStringArrayList("states");
            ArrayList<String> tookByArray = savedInstanceState.getStringArrayList("tookby");
            ArrayList<String> idArray = savedInstanceState.getStringArrayList("id");

            for(int i=0; i<titlesArray.size(); i++)
                tasks.add(new Task(idArray.get(i), titlesArray.get(i), descriptionsArray.get(i), statesArray.get(i), tookByArray.get(i)));

            sprint = new Sprint(savedInstanceState.getString("usernick"), savedInstanceState.getString("sprintTitle"));

            accFilter = savedInstanceState.getInt("accFilter");

            CardViewTaskAdapter adapter = new CardViewTaskAdapter(tasks);
            tasksList.setAdapter(adapter);
        }

        home = (ImageView) view.findViewById(R.id.home);
        todo = (ImageView) view.findViewById(R.id.todo);
        inprogress = (ImageView) view.findViewById(R.id.inprogress);
        done = (ImageView) view.findViewById(R.id.done);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blackIcons();
                home.setImageResource(R.mipmap.homegrey);
                tasksList.setAdapter(new CardViewTaskAdapter(tasks));
                getActivity().setTitle(getResources().getString(R.string.tasksBoard));
                accFilter = -1;
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
                accFilter = 0;
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
                accFilter = 1;
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
                accFilter = 2;
            }
        });

        GestureDetectorCompat detector = new GestureDetectorCompat(getActivity(), new RecyclerViewOnGestureListener());

        RecyclerViewOnItemTouchListener tasksListOnItemTouch = new RecyclerViewOnItemTouchListener(detector);

        tasksList.addOnItemTouchListener(tasksListOnItemTouch);

        switch (accFilter)
        {
            case -1:
                home.callOnClick();
                break;
            case 0:
                todo.callOnClick();
                break;
            case 1:
                inprogress.callOnClick();
                break;
            case 2:
                done.callOnClick();
                break;
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("flag", loaded_flag);
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

        if(sprint != null){
        outState.putString("usernick", sprint.getUsernick());
        outState.putString("sprintTitle", sprint.getTitle());}

        outState.putInt("accFilter", accFilter);
     }

    private void blackIcons()
    {
        home.setImageResource(R.mipmap.home);
        todo.setImageResource(R.mipmap.todo);
        inprogress.setImageResource(R.mipmap.inprogress);
        done.setImageResource(R.mipmap.done);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).accFragment = "TasksBoardFragment";
        ((MainActivity) getActivity()).showOverflowMenuRefresh(true);
        ((MainActivity) getActivity()).underlineOption();

        ((MainActivity) getActivity()).tasksBoardFragment = this;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        tasksList.setLayoutManager(layoutManager);

        if(!loaded_flag)
            new findSprintsTitles().execute();
        if(refresh_flag)
            new getSprintTasks().execute();
    }

    public void showAlertDialog()
    {
        CharSequence[] charSequences = new CharSequence[sprints.size()];

        for(int i=0; i<sprints.size(); i++)
            charSequences[i] = titleToUseless(sprints.get(i).getTitle());

        choice = 0;

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.whichOneOpen))
                .setSingleChoiceItems(charSequences, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        choice = i;
                    }
                })
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sprint = sprints.get(choice);
                        new getSprintTasks().execute();
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showEmptyBoardError()
    {
        EmptyFragment fragment = new EmptyFragment();
        fragment.setFragmentOptions(true, getResources().getString(R.string.addTasks), getResources().getString(R.string.yourSprintIsEmpty), new AddTasksFragment());
        ((MainActivity)getActivity()).updateFragment(fragment, false);
    }

    private void showSprintsError()
    {
        EmptyFragment fragment = new EmptyFragment();
        fragment.setFragmentOptions(true, getResources().getString(R.string.newSprint), getResources().getString(R.string.youtDontHaveAnySprints), new NewSprintTitleFragment());
        ((MainActivity)getActivity()).updateFragment(fragment, false);
    }

    private void onSwipeRight(){
        switch (accFilter){
            case -1:
                break;
            case 0:
                home.callOnClick();
                break;
            case 1:
                todo.callOnClick();
                break;
            case 2:
                inprogress.callOnClick();
                break;
        }
    }

    private void onSwipeLeft(){
        switch (accFilter){
            case -1:
                todo.callOnClick();
                break;
            case 0:
                inprogress.callOnClick();
                break;
            case 1:
                done.callOnClick();
                break;
            case 2:
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

    public void refresh()
    {
        new getSprintTasks().execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).showOverflowMenuRefresh(false);
    }

    private void onCardViewClicked(int position){
        TaskFragment fragment = new TaskFragment();
        fragment.myTasksFragmentOpenedBefore = false;
        CardViewTaskAdapter adapter = (CardViewTaskAdapter) tasksList.getAdapter();
        fragment.setTask(adapter.getTasks().get(position));
        fragment.setSprint(sprint);
        fragment.setAccFilter(accFilter);
        ((MainActivity)getActivity()).updateFragment(fragment, false);
        loaded_flag = false;
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

    private class getSprintTasks extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://remes.ct8.pl/gettasks.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String request;

                request = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(sprint.getTable_name(), "UTF-8");
                request += "&" + URLEncoder.encode("serverpassword", "UTF-8") + "=" + URLEncoder.encode(MainActivity.SERVERPASSWORD, "UTF-8");

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(request);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                tasks.clear();

                String legit = reader.readLine();

                if(legit.equals("FALSE"))
                    return "EMPTY";

                String readed = reader.readLine();

                while (readed != null) {
                    tasks.add(new Task(readed,reader.readLine(), reader.readLine(), reader.readLine(), reader.readLine()));
                    readed = reader.readLine();
                }

                conn.disconnect();

                return "TRUE";
            } catch (Exception e) {
                e.printStackTrace();
                return "FAILED";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            (   (ProgressBar)   getView().findViewById(R.id.tasksBoardProgresBar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("EMPTY")){
                showEmptyBoardError();
            }

            else if(s.equals("FAILED")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
            }

            (   (ProgressBar)   getView().findViewById(R.id.tasksBoardProgresBar)).setVisibility(View.INVISIBLE);

            if(accFilter != -1){
                ArrayList<Task> filtredTasks = new ArrayList<Task>();

                for(Task task : tasks){
                    if(task.getState() == accFilter)
                        filtredTasks.add(task);
                }

                tasksList.setAdapter(new CardViewTaskAdapter(filtredTasks));
            }

            else
                tasksList.setAdapter(new CardViewTaskAdapter(tasks));

            loaded_flag = true;
            refresh_flag = false;

            if(scrollToTask != null){
                int scrollTo = 0;

                ArrayList<Task> tasks = ((CardViewTaskAdapter)tasksList.getAdapter()).getTasks();

                for(int i=0; i<tasks.size(); i++){
                    if(tasks.get(i).getId() == scrollToTask.getId()){
                        scrollToTask = null; scrollTo = i; break;
                    }
                }

                tasksList.scrollToPosition(scrollTo);
            }

        }
    }

    private class findSprintsTitles extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://remes.ct8.pl/usersprint.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String request;

                request = URLEncoder.encode("usermail", "UTF-8") + "=" + URLEncoder.encode(LogInFragment.ET_LOG_STATE, "UTF-8");
                request += "&" + URLEncoder.encode("serverpassword", "UTF-8") + "=" + URLEncoder.encode(MainActivity.SERVERPASSWORD, "UTF-8");

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(request);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                sprints.clear();

                String readed = reader.readLine();

                while(readed != null)
                {
                    if(readed.equals("@FALSE@"))
                        return readed;

                    sprints.add(new Sprint(readed, reader.readLine()));

                    readed = reader.readLine();
                }

                conn.disconnect();

                return "TRUE";
            } catch (Exception e) {
                e.printStackTrace();
                return "FAILED";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            (   (ProgressBar)   getView().findViewById(R.id.tasksBoardProgresBar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            (   (ProgressBar)   getView().findViewById(R.id.tasksBoardProgresBar)).setVisibility(View.INVISIBLE);
            if(s.equals("@FALSE@")){
                showSprintsError();
            }

            else if(s.equals("FAILED")) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
                return;
            }

            else {

                if(!loaded_flag) {

                    if (sprints.size() > 1){
                        showAlertDialog();
                    }
                    else {
                        sprint = sprints.get(0);
                        new getSprintTasks().execute();
                    }
                }
            }
        }
    }

}
