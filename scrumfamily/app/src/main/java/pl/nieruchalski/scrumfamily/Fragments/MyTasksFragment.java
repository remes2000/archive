package pl.nieruchalski.scrumfamily.Fragments;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import pl.nieruchalski.scrumfamily.Adapters.CardViewMyTaskAdapter;
import pl.nieruchalski.scrumfamily.Database.SprintsDatabaseHelper;
import pl.nieruchalski.scrumfamily.HelpingClasses.RecyclerViewOnItemTouchListener;
import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.HelpingClasses.Task;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyTasksFragment extends Fragment {

    public int accFilter = 0;

    private Boolean loaded_flag = false;
    private Boolean canDestroyFlag = false;

    public Task scrollToTask = null;
    public Sprint scrollToSprint = null;

    private RecyclerView recyclerView;
    private ImageView inprogress;
    private ImageView done;
    private ImageView home;

    private ArrayList<Task> tasks;
    private ArrayList<Sprint> sprints;

    public MyTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).accFragment = "MyTasksFragment";
        ((MainActivity) getActivity()).showOverflowMenuRefreshYourTasks(true);
        ((MainActivity) getActivity()).underlineOption();
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.yoursTasks));
        ((MainActivity) getActivity()).myTasksFragment = this;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        if(!loaded_flag)
            new getTasks().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_tasks, container, false);

        tasks = new ArrayList<Task>();
        sprints = new ArrayList<Sprint>();

        recyclerView = (RecyclerView) view.findViewById(R.id.myTasksRecyclerView);
        inprogress = (ImageView) view.findViewById(R.id.myInProgressTasks);
        done = (ImageView) view.findViewById(R.id.myDoneTasks);
        home = (ImageView) view.findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inprogress.setImageResource(R.mipmap.inprogress);
                done.setImageResource(R.mipmap.done);
                home.setImageResource(R.mipmap.homegrey);

                accFilter = 0;

                recyclerView.setAdapter(new CardViewMyTaskAdapter(tasks, sprints, getActivity().getApplicationContext()));

            }
        });

        inprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inprogress.setImageResource(R.mipmap.inprogressgrey);
                done.setImageResource(R.mipmap.done);
                home.setImageResource(R.mipmap.home);

                ArrayList<Task> inprogressTasks = new ArrayList<Task>();
                ArrayList<Sprint> inprogressSprints = new ArrayList<Sprint>();

                for(int i=0; i<tasks.size(); i++){
                    if(tasks.get(i).getState() == 1){
                        inprogressTasks.add(tasks.get(i));
                        inprogressSprints.add(sprints.get(i));
                    }
                }

                accFilter = 1;

                recyclerView.setAdapter(new CardViewMyTaskAdapter(inprogressTasks, inprogressSprints, getActivity().getApplicationContext()));

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done.setImageResource(R.mipmap.done_gray);
                inprogress.setImageResource(R.mipmap.inprogress);
                home.setImageResource(R.mipmap.home);

                ArrayList<Task> doneTasks = new ArrayList<Task>();
                ArrayList<Sprint> doneSprints = new ArrayList<Sprint>();

                for(int i=0; i<tasks.size(); i++){
                    if(tasks.get(i).getState() == 2){
                        doneTasks.add(tasks.get(i));
                        doneSprints.add(sprints.get(i));
                    }
                }

                accFilter = 2;

                recyclerView.setAdapter(new CardViewMyTaskAdapter(doneTasks, doneSprints, getActivity().getApplicationContext()));

            }
        });

        if(savedInstanceState != null){
            accFilter = savedInstanceState.getInt("accFilter");
            loaded_flag = savedInstanceState.getBoolean("loaded_flag");

            ArrayList<Integer> ids = savedInstanceState.getIntegerArrayList("ids");
            ArrayList<String> titles = savedInstanceState.getStringArrayList("taskTitles");
            ArrayList<String> descriptions = savedInstanceState.getStringArrayList("taskDescriptions");
            ArrayList<Integer> states = savedInstanceState.getIntegerArrayList("states");
            ArrayList<String> tookbys = savedInstanceState.getStringArrayList("tookbys");

            for(int i=0; i<ids.size(); i++){
                tasks.add(new Task(
                        Integer.toString(ids.get(i)),
                        titles.get(i),
                        descriptions.get(i),
                        Integer.toString(states.get(i)),
                        tookbys.get(i)
                ));
            }

            ArrayList<String> usernicks = savedInstanceState.getStringArrayList("usernicks");
            ArrayList<String> sprintTitles = savedInstanceState.getStringArrayList("sprintTitles");

            for(int i=0; i<usernicks.size(); i++){
                sprints.add(new Sprint(
                        usernicks.get(i),
                        sprintTitles.get(i)
                ));
            }

        }

        switch (accFilter){
            case 0:
                home.callOnClick();
                break;
            case 1:
                inprogress.callOnClick();
                break;
            case 2:
                done.callOnClick();
                break;
        }

        GestureDetectorCompat detector = new GestureDetectorCompat(getActivity(), new RecyclerViewOnGestureListener());

        RecyclerViewOnItemTouchListener tasksListOnItemTouch = new RecyclerViewOnItemTouchListener(detector);

        recyclerView.addOnItemTouchListener(tasksListOnItemTouch);

        return view;
    }

    public void refresh(){
        if(!MainActivity.OFFLINE_MODE)
            new getTasks().execute();
    }


    private void onCardViewClicked(int position){
        TaskFragment fragment = new TaskFragment();
        fragment.myTasksFragmentOpenedBefore = true;
        fragment.setAccFilter(accFilter);
        CardViewMyTaskAdapter adapter = (CardViewMyTaskAdapter) recyclerView.getAdapter();
        fragment.setTask(adapter.getTasks().get(position));
        fragment.setSprint(sprints.get(position));
        ((MainActivity)getActivity()).updateFragment(fragment, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("accFilter", accFilter);

        ArrayList<Integer> ids = new ArrayList<Integer>();
        ArrayList<String> titles = new ArrayList<String>();
        ArrayList<String> descriptions = new ArrayList<String>();
        ArrayList<Integer> states = new ArrayList<Integer>();
        ArrayList<String> tookbys = new ArrayList<String>();

        for(int i=0; i<tasks.size(); i++) {
            ids.add(tasks.get(i).getId());
            titles.add(tasks.get(i).getTitle());
            descriptions.add(tasks.get(i).getDescription());
            states.add(tasks.get(i).getState());
            tookbys.add(tasks.get(i).getTookBy());
        }

        outState.putIntegerArrayList("ids", ids);
        outState.putStringArrayList("taskTitles", titles);
        outState.putStringArrayList("taskDescriptions", descriptions);
        outState.putIntegerArrayList("states", states);
        outState.putStringArrayList("tookbys", tookbys);

        ArrayList<String> usernicks = new ArrayList<String>();
        ArrayList<String> sprintTitles = new ArrayList<String>();

        for(int i=0; i<sprints.size(); i++){
            usernicks.add(sprints.get(i).getUsernick());
            sprintTitles.add(sprints.get(i).getTitle());
        }

        outState.putStringArrayList("usernicks", usernicks);
        outState.putStringArrayList("sprintTitles", sprintTitles);

        outState.putBoolean("loaded_flag", loaded_flag);
    }

    @Override
    public void onDestroyView() {

        if(LogInFragment.REMEMBER_ME_STATE)
            new saveTasks().execute(getActivity().getApplicationContext());

        ((MainActivity) getActivity()).showOverflowMenuRefreshYourTasks(false);
        super.onDestroyView();
    }

    private void onSwipeRight(){
        switch (accFilter){
            case 0:
                break;
            case 1:
                home.callOnClick();
                break;
            case 2:
                inprogress.callOnClick();
                break;
        }
    }

    private void onSwipeLeft(){
        switch (accFilter){
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

    private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            int position = recyclerView.getChildPosition(view);

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

    private class getTasks extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://remes.ct8.pl/mytasks.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String request;

                request = URLEncoder.encode("usernick", "UTF-8") + "=" + URLEncoder.encode(MainActivity.NICKNAME, "UTF-8");
                request += "&" + URLEncoder.encode("serverpassword", "UTF-8") + "=" + URLEncoder.encode(MainActivity.SERVERPASSWORD, "UTF-8");

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(request);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                tasks.clear();

                String readed = reader.readLine();

                if(readed == null)
                    return "EMPTY";

                while (readed != null) {
                    sprints.add(new Sprint(readed));

                    String id = reader.readLine();
                    String title = reader.readLine();
                    String description = reader.readLine();
                    String state = reader.readLine();
                    String tookby = reader.readLine();

                    tasks.add(new Task(id, title, description, state, tookby));

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
            (   (ProgressBar)   getView().findViewById(R.id.myTasksProgressBar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            (   (ProgressBar)   getView().findViewById(R.id.myTasksProgressBar)).setVisibility(View.INVISIBLE);

            if(s.equals("EMPTY")){
                EmptyFragment emptyFragment = new EmptyFragment();
                Fragment taskBoardFragment = new TasksBoardFragment();
                emptyFragment.setFragmentOptions(true, getResources().getString(R.string.goToTaskBoard), getResources().getString(R.string.youDontHaveAnyTasks), taskBoardFragment);
                ((MainActivity) getActivity()).updateFragment(emptyFragment, false);
                loaded_flag = true;
            }

            else if(s.equals("FAILED")){
                if(!MainActivity.OFFLINE_MODE)
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
                new readTasksFromDatabase().execute();
                return;
            }

            loaded_flag = true;

            switch (accFilter){
                case 0:
                    home.callOnClick();
                    break;
                case 1:
                     inprogress.callOnClick();
                    break;
                case 2:
                    done.callOnClick();
            }

            if((scrollToSprint != null)&&(scrollToTask != null)){

                for(int i=0; i<tasks.size(); i++){
                    if((tasks.get(i).getId() == scrollToTask.getId())&&(sprints.get(i).getTable_name().equals(scrollToSprint.getTable_name()))){
                        recyclerView.scrollToPosition(i);
                        scrollToTask = null;
                        scrollToSprint = null;
                        break;
                    }
                }

            }

        }
    }

    private class saveTasks extends AsyncTask<Context, Void, Void>{
        @Override
        protected Void doInBackground(Context... contexts) {

            Context context = contexts[0];

            SQLiteOpenHelper helper = new SprintsDatabaseHelper(context);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL("delete from "+ "MYTASKS");

            ContentValues valuesToInsert = new ContentValues(tasks.size());

            for(int i=0; i<tasks.size(); i++){

                valuesToInsert.put("TABLENAME", sprints.get(i).getTable_name());
                valuesToInsert.put("ID", tasks.get(i).getId());
                valuesToInsert.put("TITLE", tasks.get(i).getTitle());
                valuesToInsert.put("DESCRIPTION", tasks.get(i).getDescription());
                valuesToInsert.put("STATE", tasks.get(i).getState());
                valuesToInsert.put("TOOKBY", tasks.get(i).getTookBy());

                db.insert("MYTASKS", null, valuesToInsert);

                valuesToInsert.clear();

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class readTasksFromDatabase extends  AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {

            SQLiteOpenHelper helper = new SprintsDatabaseHelper(getActivity().getApplicationContext());
            SQLiteDatabase db = helper.getWritableDatabase();

            Cursor cursor = db.query("MYTASKS", null, null, null, null, null, null);

            tasks.clear();

            if(cursor.moveToFirst()){
                sprints.add(new Sprint(cursor.getString(1)));
                tasks.add(new Task(
                        Integer.toString(cursor.getInt(2)),
                        cursor.getString(3),
                        cursor.getString(4),
                        Integer.toString(cursor.getInt(5)),
                        cursor.getString(6)
                ));
            }
            else
                return null;

            while(cursor.moveToNext()){
                sprints.add(new Sprint(cursor.getString(1)));
                tasks.add(new Task(
                        Integer.toString(cursor.getInt(2)),
                        cursor.getString(3),
                        cursor.getString(4),
                        Integer.toString(cursor.getInt(5)),
                        cursor.getString(6)
                ));
            }
            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            (   (ProgressBar)   getView().findViewById(R.id.myTasksProgressBar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.d("taskssize", Integer.toString(tasks.size()));

            (   (ProgressBar)   getView().findViewById(R.id.myTasksProgressBar)).setVisibility(View.INVISIBLE);

            loaded_flag = true;

            switch (accFilter){
                case 0:
                    home.callOnClick();
                    break;
                case 1:
                    inprogress.callOnClick();
                    break;
                case 2:
                    done.callOnClick();
            }

        }
    }

}
