package pl.nieruchalski.scrumfamily.Fragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import pl.nieruchalski.scrumfamily.Adapters.CardViewSprintsSummariesAdapter;
import pl.nieruchalski.scrumfamily.Adapters.CardViewTaskAdapter;
import pl.nieruchalski.scrumfamily.Database.SprintsDatabaseHelper;
import pl.nieruchalski.scrumfamily.Fragments.SprintSummaryTaskBoard.SprintSummaryTaskBoard;
import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.HelpingClasses.Task;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SprintSummaryFragment extends Fragment {

    private RecyclerView sprintsList;
    private ArrayList<Sprint> sprints;
    private ArrayList<String> startDates;
    private ArrayList<String> endDates;
    private ArrayList<Boolean> saved;
    private ArrayList<Integer> ids;

    private int pos = -1;

    public SprintSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).accFragment = "SprintSummaryFragment";
        ((MainActivity) getActivity()).underlineOption();
        ((MainActivity) getActivity()).sprintSummaryFragment = this;
        ((MainActivity) getActivity()).showOverflowMenuRefreshSummaries(true);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.sprintSummaries));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        sprintsList.setLayoutManager(layoutManager);

        new readFromDatabase().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sprint_summary, container, false);

        sprints = new ArrayList<Sprint>();
        startDates = new ArrayList<String>();
        endDates = new ArrayList<String>();
        saved = new ArrayList<Boolean>();
        ids = new ArrayList<Integer>();

        sprintsList = (RecyclerView) view.findViewById(R.id.sprintList);

        return view;
    }

    public void onCardViewClicked(int position) {

        new getSummaryData(true).execute(sprints.get(position));
        pos = position;

    }

    public void onSavedCardViewClicked(int position){
        SQLiteOpenHelper helper = new SprintsDatabaseHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("SAVEDSUMMARIES", null, "SPRINT_ID = ?", new String[]{Integer.toString(ids.get(position))}, null, null, null);

        cursor.moveToFirst();
        SprintSummaryDataFragment fragment = new SprintSummaryDataFragment();
        fragment.setMostDoneUser(cursor.getString(3));
        fragment.setMostDoneUserNumber(cursor.getString(4));
        fragment.setDoneNumber(cursor.getString(5));
        fragment.setNotDoneNumber(cursor.getString(6));
        fragment.setStartDate(cursor.getString(7));
        fragment.setEndDate(cursor.getString(8));
        fragment.setTitle(titleToUseless(sprints.get(position).getTitle()));
        fragment.setSprint(sprints.get(position));
        fragment.setSaved(saved.get(position));
        fragment.setSprint_id(ids.get(position));
        fragment.setPositionInRecyclerView(position);

        ((MainActivity)getActivity()).updateFragment(fragment, false);

        cursor.close();
        db.close();

    }

    public void refresh(){
        sprints.clear();
        startDates.clear();
        endDates.clear();
        saved.clear();
        ids.clear();
        new readFromDatabase().execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).showOverflowMenuRefreshSummaries(false);
    }

    public void onSaveButtonClicked(int position){
        new getSummaryData(false, position).execute(sprints.get(position));
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

    private class getEndedSprints extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://remes.ct8.pl/userendsprints.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String request;

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                request = URLEncoder.encode("usermail", "UTF-8") + "=" + URLEncoder.encode(LogInFragment.ET_LOG_STATE, "UTF-8");
                request += "&" + URLEncoder.encode("serverpassword", "UTF-8") + "=" + URLEncoder.encode(MainActivity.SERVERPASSWORD, "UTF-8");

                writer.write(request);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String readed;

                readed = reader.readLine();

                if (readed == null)
                    return "EMPTY";

                while (readed != null) {

                    Boolean flag = true;

                    int id = Integer.parseInt(readed);
                    String tablename = reader.readLine();
                    String startDate = reader.readLine();
                    String endDate = reader.readLine();

                    for(Integer savedID : ids){
                        if(savedID == id)
                            flag = false;
                    }

                    if(flag){
                        ids.add(id);
                        sprints.add(new Sprint(tablename));
                        startDates.add(startDate);
                        endDates.add(endDate);
                        saved.add(false);
                    }

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
            ((ProgressBar) getView().findViewById(R.id.sprintSummaryProgressBar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ((ProgressBar) getView().findViewById(R.id.sprintSummaryProgressBar)).setVisibility(View.INVISIBLE);

            if (s.equals("TRUE")) {
            } else if (s.equals("FAILED")) {
                if(!MainActivity.OFFLINE_MODE)
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
            }
            else if ((s.equals("EMPTY")&&(sprints.size() == 0))) {
                EmptyFragment fragment = new EmptyFragment();
                fragment.setFragmentOptions(false, null, getResources().getString(R.string.noEndedSprints), null);
                ((MainActivity)getActivity()).updateFragment(fragment, false);
            }
            sprintsList.setAdapter(new CardViewSprintsSummariesAdapter(sprints, startDates, endDates, saved, getActivity().getApplicationContext(), SprintSummaryFragment.this));
        }
    }

    private class getSummaryData extends AsyncTask<Sprint, Void, ArrayList<String>>{

        private Boolean openFragment;
        private int position;

        public getSummaryData(Boolean openFragment){
            this.openFragment = openFragment;
        }

        public getSummaryData(Boolean openFragment, int position){
            this.openFragment = openFragment;
            this.position = position;
        }

        @Override
        protected ArrayList<String> doInBackground(Sprint... sprints) {
            try {
                URL url = new URL("http://remes.ct8.pl/sumsprint.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Sprint sprint = sprints[0];

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                String request;

                request = URLEncoder.encode("usernick", "UTF-8") + "=" + URLEncoder.encode(sprint.getUsernick(), "UTF-8");
                request += "&" + URLEncoder.encode("sprinttitle", "UTF-8") + "=" + URLEncoder.encode(sprint.getTitle(), "UTF-8");
                request += "&" + URLEncoder.encode("serverpassword", "UTF-8") + "=" + URLEncoder.encode(MainActivity.SERVERPASSWORD, "UTF-8");

                writer.write(request);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                ArrayList<String> outputs = new ArrayList<String>();

                String readed = reader.readLine();

                while(readed!=null){
                    if(!readed.equals(""))
                        outputs.add(readed);
                    readed = reader.readLine();
                }

                conn.disconnect();

                return outputs;

            } catch (Exception e) {
                e.printStackTrace();
                ArrayList<String> failed = new ArrayList<String>();
                failed.add("FAILED");
                return failed;
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);

            if(strings.get(0).equals("FAILED")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
                if(!openFragment)
                    ((CardViewSprintsSummariesAdapter) sprintsList.getAdapter()).changeToNormalView(pos);
                return;
            }
            else if(openFragment) {
                SprintSummaryDataFragment fragment = new SprintSummaryDataFragment();
                fragment.setMostDoneUser(strings.get(0));
                fragment.setMostDoneUserNumber(strings.get(1));
                fragment.setDoneNumber(strings.get(2));
                fragment.setNotDoneNumber(strings.get(3));
                fragment.setStartDate(strings.get(4));
                fragment.setEndDate(strings.get(5));
                fragment.setSprint(sprints.get(pos));
                fragment.setSaved(saved.get(pos));
                fragment.setSprint_id(ids.get(pos));
                fragment.setPositionInRecyclerView(pos);

                if (pos != -1)
                    fragment.setTitle(titleToUseless(sprints.get(pos).getTitle()));

                ((MainActivity) getActivity()).updateFragment(fragment, false);
            }
            else if(!openFragment){
                new writeToDatabase(position).execute(new String[]{strings.get(0), strings.get(1), strings.get(2), strings.get(3), strings.get(4), strings.get(5)});
            }

        }
    }

    private class writeToDatabase extends AsyncTask<String[], Void, Void>{

        private int position;

        public writeToDatabase(int position){
            this.position = position;
        }

        @Override
        protected Void doInBackground(String[]... strings) {

            String[] values = strings[0];

            SQLiteOpenHelper helper = new SprintsDatabaseHelper(getActivity().getApplicationContext());
            SQLiteDatabase db = helper.getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put("SPRINT_ID", ids.get(position));
            cv.put("TABLENAME", sprints.get(position).getTable_name());
            cv.put("MOSTDONEUSER", values[0]);
            cv.put("MOSTDONEUSERNUMBER", values[1]);
            cv.put("DONENUMBER", values[2]);
            cv.put("NOTDONENUMBER", values[3]);
            cv.put("STARTDATE", values[4]);
            cv.put("ENDDATE", values[5]);

            db.insert("SAVEDSUMMARIES", null, cv);

            db.close();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((CardViewSprintsSummariesAdapter) sprintsList.getAdapter()).showProgressBar(position);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new getSprintTasks(position).execute(sprints.get(position));
        }
    }

    private class writeTasksToDatabase extends AsyncTask<Void, Void, Void>{

        ArrayList<Task> tasksToSave;
        int position;

        public writeTasksToDatabase(ArrayList<Task> tasks, int position)
        {
            this.tasksToSave = tasks;
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SQLiteOpenHelper helper = new SprintsDatabaseHelper(getActivity().getApplicationContext());
            SQLiteDatabase db = helper.getWritableDatabase();

            ContentValues cv = new ContentValues();

            for(int i=0; i<tasksToSave.size(); i++){
                cv.put("SPRINT_ID", ids.get(position));
                cv.put("ID", tasksToSave.get(i).getId());
                cv.put("TITLE", tasksToSave.get(i).getTitle());
                cv.put("DESCRIPTION", tasksToSave.get(i).getDescription());
                cv.put("STATE", tasksToSave.get(i).getState());
                cv.put("TOOKBY", tasksToSave.get(i).getTookBy());

                db.insert("SAVEDSPRINTS", null, cv);

                cv.clear();

            }

            db.close();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ((CardViewSprintsSummariesAdapter) sprintsList.getAdapter()).hideProgressBar(position);
            saved.set(position, true);
        }
    }

    private class getSprintTasks extends AsyncTask<Sprint, Void, String>
    {

        ArrayList<Task> tasks;
        int position;

        public getSprintTasks(int position){
            this.tasks = new ArrayList<Task>();
            this.position = position;
        }

        @Override
        protected String doInBackground(Sprint... sprints) {
            try {
                URL url = new URL("http://remes.ct8.pl/gettasks.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Sprint sprint = sprints[0];

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
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("FAILED")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
                ((CardViewSprintsSummariesAdapter) sprintsList.getAdapter()).changeToNormalView(pos);
                return;
            }

            new writeTasksToDatabase(tasks, position).execute();

        }
    }

    private class readFromDatabase extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            SQLiteOpenHelper helper = new SprintsDatabaseHelper(getActivity().getApplicationContext());
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query("SAVEDSUMMARIES", null, null, null, null, null, null);

            if(cursor.moveToFirst()){
                ids.add(cursor.getInt(1));
                sprints.add(new Sprint(cursor.getString(2)));
                startDates.add(cursor.getString(7));
                endDates.add(cursor.getString(8));
                saved.add(true);
            }
            else
                return null;

            while(cursor.moveToNext()) {
                ids.add(cursor.getInt(1));
                sprints.add(new Sprint(cursor.getString(2)));
                startDates.add(cursor.getString(7));
                endDates.add(cursor.getString(8));
                saved.add(true);
            }

            cursor.close();
            db.close();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new getEndedSprints().execute();
        }
    }

}
