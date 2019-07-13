package pl.nieruchalski.scrumfamily.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

import pl.nieruchalski.scrumfamily.Adapters.CardViewTaskAdapter;
import pl.nieruchalski.scrumfamily.Adapters.InvitationAdapter;
import pl.nieruchalski.scrumfamily.Database.SprintsDatabaseHelper;
import pl.nieruchalski.scrumfamily.Fragments.SprintSummaryTaskBoard.SprintSummaryTaskBoard;
import pl.nieruchalski.scrumfamily.HelpingClasses.Invitation;
import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.HelpingClasses.Task;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SprintSummaryDataFragment extends Fragment {

    private Sprint sprint;
    public Boolean saved;
    private Integer sprint_id;
    private int positionInRecyclerView;

    private String mostDoneUser;
    private String mostDoneUserNumber;
    private String doneNumber;
    private String notDoneNumber;
    private String startDate;
    private String endDate;
    private String durationTime;
    private String title;

    private TextView mostDoneTV;
    private TextView doneNumberTV;
    private TextView notDoneNumberTV;
    private TextView startDateTV;
    private TextView endDateTV;
    private TextView durationTimeTV;
    private Button showTasksBoard;
    private ArrayList<Task> tasks;

    public SprintSummaryDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).accFragment = "SprintSummaryDataFragment";
        ((MainActivity) getActivity()).underlineOption();
        ((MainActivity) getActivity()).setTitle(title);
        ((MainActivity) getActivity()).sprintSummaryDataFragment = this;

        if(saved)
            ((MainActivity) getActivity()).showOverflowMenuDeleteSummary(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sprint_summary_data, container, false);

        mostDoneTV = (TextView) view.findViewById(R.id.mostTaskDoneNick);
        doneNumberTV = (TextView) view.findViewById(R.id.doneTasksNumber);
        notDoneNumberTV = (TextView) view.findViewById(R.id.notDoneTasksNumber);
        startDateTV = (TextView) view.findViewById(R.id.startDateNumber);
        endDateTV = (TextView) view.findViewById(R.id.endDateNumber);
        durationTimeTV = (TextView) view.findViewById(R.id.durationTime);
        showTasksBoard = (Button) view.findViewById(R.id.goToTaskBoard);
        tasks = new ArrayList<Task>();

        if(savedInstanceState != null){
            mostDoneUser = savedInstanceState.getString("mostDoneUser");
            mostDoneUserNumber = savedInstanceState.getString("mostDoneUserNumber");
            doneNumber = savedInstanceState.getString("doneNumber");
            notDoneNumber = savedInstanceState.getString("notDoneNumber");
            startDate = savedInstanceState.getString("startDate");
            endDate = savedInstanceState.getString("endDate");
            title = savedInstanceState.getString("title");
            sprint = new Sprint(savedInstanceState.getString("tablename"));
            saved = savedInstanceState.getBoolean("saved");
            sprint_id = savedInstanceState.getInt("sprint_id");
            positionInRecyclerView = savedInstanceState.getInt("positionInRecyclerView");
        }

        if(mostDoneUser.equals("@NULL"))
            mostDoneTV.setText(getResources().getString(R.string.didintChoose));
        else
            mostDoneTV.setText(mostDoneUser + "(" + mostDoneUserNumber + ")");
        doneNumberTV.setText(doneNumber);
        notDoneNumberTV.setText(notDoneNumber);
        startDateTV.setText(startDate);
        endDateTV.setText(endDate);

        showTasksBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saved)
                    readTasksFromLocalDatabase();
                else
                    readTasksFromInternet();
            }
        });

        String[] startDateArray = startDate.split("-");
        String[] endDateArray = endDate.split("-");

        Calendar startDateCalendar = Calendar.getInstance();
        Calendar endDateCalendar = Calendar.getInstance();

        startDateCalendar.set(Integer.parseInt(startDateArray[0]), Integer.parseInt(startDateArray[1]), Integer.parseInt(startDateArray[2]));
        endDateCalendar.set(Integer.parseInt(endDateArray[0]), Integer.parseInt(endDateArray[1]), Integer.parseInt(endDateArray[2]));

        long durationInDays = ((endDateCalendar.getTimeInMillis() + 86400000) - startDateCalendar.getTimeInMillis()) / 86400000;

        if(durationInDays == 1)
            durationTimeTV.setText(Integer.toString((int) durationInDays) + " " + getResources().getString(R.string.alternativeDays));
        else
            durationTimeTV.setText(Integer.toString((int) durationInDays) + " " + getResources().getString(R.string.days));

        return view;

    }

    private void readTasksFromInternet()
    {
        new getSprintTasks().execute();
    }

    private void readTasksFromLocalDatabase(){
        SQLiteOpenHelper helper = new SprintsDatabaseHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("SAVEDSPRINTS", null, null, null, null, null, null);

        if(cursor.moveToFirst()){
            tasks.add(new Task(
                    Integer.toString(cursor.getInt(2)),
                    cursor.getString(3),
                    cursor.getString(4),
                    Integer.toString(cursor.getInt(5)),
                    cursor.getString(6)
            ));
        }
        else{
            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.thisSprintWasEmpty), Toast.LENGTH_SHORT).show();
            return;
        }

        while(cursor.moveToNext()){
            tasks.add(new Task(
                    Integer.toString(cursor.getInt(2)),
                    cursor.getString(3),
                    cursor.getString(4),
                    Integer.toString(cursor.getInt(5)),
                    cursor.getString(6)
            ));
        }

        SprintSummaryTaskBoard fragment = new SprintSummaryTaskBoard();
        fragment.tasks = tasks;
        ((MainActivity) getActivity()).updateFragment(fragment, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mostDoneUser", mostDoneUser);
        outState.putString("mostDoneUserNumber", mostDoneUserNumber);
        outState.putString("doneNumber", doneNumber);
        outState.putString("notDoneNumber", notDoneNumber);
        outState.putString("startDate", startDate);
        outState.putString("endDate", endDate);
        outState.putString("title", title);
        outState.putString("tablename", sprint.getTable_name());
        outState.putBoolean("saved", saved);
        outState.putInt("sprint_id", sprint_id);
        outState.putInt("positionInRecyclerView", positionInRecyclerView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).showOverflowMenuDeleteSummary(false);
    }

    public void showDeleteSummary(){
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.doYouWantDeleteSummary))
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteSummary();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ;
                    }
                })
                .create();
        dialog.show();
    }

    public void deleteSummary(){
        SQLiteOpenHelper helper = new SprintsDatabaseHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("SAVEDSUMMARIES", "SPRINT_ID = ?", new String[]{Integer.toString(sprint_id)});
        db.delete("SAVEDSPRINTS", "SPRINT_ID = ?", new String[]{Integer.toString(sprint_id)});
        db.close();
        ((MainActivity)getActivity()).onBackPressed();
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

    public void onBackPressed(){
        SprintSummaryFragment fragment = new SprintSummaryFragment();
        ((MainActivity) getActivity()).updateFragment(fragment, false);
    }

    public void setDurationTime(String durationTime) {
        this.durationTime = durationTime;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setNotDoneNumber(String notDoneNumber) {
        this.notDoneNumber = notDoneNumber;
    }

    public void setDoneNumber(String doneNumber) {
        this.doneNumber = doneNumber;
    }

    public void setMostDoneUserNumber(String mostDoneUserNumber) {
        this.mostDoneUserNumber = mostDoneUserNumber;
    }

    public void setMostDoneUser(String mostDoneUser) {
        this.mostDoneUser = mostDoneUser;
    }

    public void setTitle(String title){this.title = title;}

    public void setSaved(Boolean saved) {
        this.saved = saved;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public void setSprint_id(Integer sprint_id) {
        this.sprint_id = sprint_id;
    }

    public void setPositionInRecyclerView(int positionInRecyclerView) {
        this.positionInRecyclerView = positionInRecyclerView;
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
            (   (ProgressBar)   getView().findViewById(R.id.sprintDataProgressBar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            (   (ProgressBar)   getView().findViewById(R.id.sprintDataProgressBar)).setVisibility(View.INVISIBLE);

            if(s.equals("EMPTY")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.thisSprintWasEmpty), Toast.LENGTH_SHORT).show();
            }

            else if(s.equals("FAILED")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
            }

            else if(s.equals("TRUE")){
                SprintSummaryTaskBoard fragment = new SprintSummaryTaskBoard();
                fragment.tasks = tasks;
                ((MainActivity) getActivity()).updateFragment(fragment, false);
            }

        }
    }

}
