package pl.nieruchalski.scrumfamily.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InterfaceAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.HelpingClasses.Task;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment {

    public Boolean myTasksFragmentOpenedBefore = false;

    private Boolean isAlternativeLayout = false;

    private int choice = 0;
    private int whichOneUserChoice = 0;
    private int accFilter = -1;

    private Task task;
    private Sprint sprint;
    private TextView title;
    private TextView description;
    private TextView state;
    private TextView claimedByText;
    private TextView claimedBy;
    private Button button;
    private Button buttonresettask;
    private ImageView imageView;
    private TextView sprintTitle;

    private ArrayList<String> users;

    public TaskFragment() {
        // Required empty public constructor
    }

    public void setTask(Task task)
    {
        this.task = task;
    }

    public void setSprint(Sprint sprint){this.sprint = sprint;}

    public void setAccFilter(int accFilter){this.accFilter = accFilter;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task, container, false);

        if(savedInstanceState != null)
        {
            task = new Task(savedInstanceState.getString("taskId"), savedInstanceState.getString("taskTitle"),
                    savedInstanceState.getString("taskDescription"), savedInstanceState.getString("taskState"),
                    savedInstanceState.getString("tasktookby"));
            sprint = new Sprint(savedInstanceState.getString("sprintUsernick") ,savedInstanceState.getString("sprintTitle"));

            myTasksFragmentOpenedBefore = savedInstanceState.getBoolean("myTasksFragmentOpenedBefore");
        }

        if((task.getState()==1)&&(!task.getTookBy().equals(MainActivity.NICKNAME))){
            view = inflater.inflate(R.layout.fragment_task_alternativelayout, container, false);
            isAlternativeLayout = true;
        }

        if((task.getState()==2)&&(!task.getTookBy().equals(MainActivity.NICKNAME))){
            view = inflater.inflate(R.layout.fragment_task_alternativelayout, container, false);
            isAlternativeLayout = true;
        }

        users = new ArrayList<String>();

        title = (TextView) view.findViewById(R.id.taskTitle);
        description = (TextView) view.findViewById(R.id.taskDescription);
        state = (TextView) view.findViewById(R.id.state);
        claimedByText = (TextView) view.findViewById(R.id.claimedByText);
        claimedBy = (TextView) view.findViewById(R.id.claimedBy);
        sprintTitle = (TextView) view.findViewById(R.id.sprintTitle);
        imageView = (ImageView) view.findViewById(R.id.imageViewState);

        if(!isAlternativeLayout){
            button = (Button) view.findViewById(R.id.claimTaskButton);
            buttonresettask = (Button) view.findViewById(R.id.resetTaskButton);}

        title.setText(task.getTitle());
        description.setText(task.getDescription());
        sprintTitle.setText(titleToUseless(sprint.getTitle()));

        switch (task.getState())
        {
            case 0:
                state.setText(getResources().getString(R.string.todo));
                claimedByText.setText(getResources().getString(R.string.taskisstealtodo));
                button.setText(getResources().getString(R.string.claimTask));
                buttonresettask.setVisibility(View.INVISIBLE);
                imageView.setImageResource(R.mipmap.todo);
                view.setBackgroundColor(Color.rgb(255, 196, 206));
                break;
            case 1:
                state.setText(getResources().getString(R.string.inprogress));
                claimedByText.setText(getResources().getString(R.string.doingTask));
                claimedBy.setText(task.getTookBy());
                imageView.setImageResource(R.mipmap.inprogress);
                view.setBackgroundColor(Color.rgb(188, 229, 255));

                if(!isAlternativeLayout) {
                    button.setText(getResources().getString(R.string.taskDone));
                }

                break;
            case 2:
                state.setText(getResources().getString(R.string.done));
                claimedByText.setText(getResources().getString(R.string.doneTask));
                claimedBy.setText(task.getTookBy());
                imageView.setImageResource(R.mipmap.done);
                view.setBackgroundColor(Color.rgb(188, 255, 190));

                if(!isAlternativeLayout)
                    button.setVisibility(View.INVISIBLE);

                break;
        }

        if(!isAlternativeLayout) {

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (button.getText().equals(getResources().getString(R.string.claimTask))) {
                        new claimTask().execute(new String[]{"1", MainActivity.NICKNAME});
                    } else if (button.getText().equals(getResources().getString(R.string.taskDone))) {
                        new claimTask().execute(new String[]{"2", MainActivity.NICKNAME});
                    }
                }
            });

            buttonresettask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new claimTask().execute(new String[]{"0", "0"});
                }
            });

        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).accFragment = "TaskFragment";
        ((MainActivity) getActivity()).taskFragment = this;
        ((MainActivity) getActivity()).underlineOption();

        if(MainActivity.NICKNAME.equals(sprint.getUsernick()))
            ((MainActivity) getActivity()).showOverflowMenuManageTask(true);

    }

    public void showManageOptions()
    {
        CharSequence[] options = new CharSequence[3];

        options[0] = getResources().getString(R.string.deleteTask);
        options[1] = getResources().getString(R.string.resetTask);
        options[2] = getResources().getString(R.string.ascribeTask);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.chooseAction))
                .setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        choice = i;
                    }
                })
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (choice)
                        {
                            case 0: // delete task
                                new deleteTask().execute();
                                break;
                            case 1: // reset task
                                new claimTask().execute(new String[]{"0", "0"});
                                break;
                            case 2: //ascribe
                                new getUserNicks().execute();
                                break;
                        }

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

    private void showAscribeTask(){

        CharSequence[] usersToShow = new CharSequence[users.size()];

        for(int i=0; i<users.size(); i++)
            usersToShow[i] = users.get(i);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.whichOneUser))
                .setSingleChoiceItems(usersToShow, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        whichOneUserChoice = i;
                    }
                })
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new claimTask().execute(new String[]{"1", users.get(whichOneUserChoice)});
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("taskTitle", task.getTitle());
        outState.putString("taskDescription", task.getDescription());
        outState.putString("tasktookby", task.getTookBy());
        outState.putString("taskState", Integer.toString(task.getState()));
        outState.putString("taskId", Integer.toString(task.getId()));

        outState.putString("sprintTitle", sprint.getTitle());
        outState.putString("sprintUsernick", sprint.getUsernick());

        outState.putBoolean("myTasksFragmentOpenedBefore", myTasksFragmentOpenedBefore);

    }

    public void onBackPressed()
    {
        if(myTasksFragmentOpenedBefore){
            MyTasksFragment fragment = new MyTasksFragment();
            fragment.accFilter = this.accFilter;
            fragment.scrollToTask = this.task;
            fragment.scrollToSprint = this.sprint;
            ((MainActivity) getActivity()).updateFragment(fragment, false);
        }
        else {
            TasksBoardFragment fragment = new TasksBoardFragment();
            fragment.loaded_flag = true;
            fragment.refresh_flag = true;
            fragment.sprint = this.sprint;
            fragment.scrollToTask = this.task;
            fragment.accFilter = this.accFilter;
            ((MainActivity) getActivity()).updateFragment(fragment, false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).showOverflowMenuManageTask(false);
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

    private class getUserNicks extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://remes.ct8.pl/getmembers.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String request;

                request = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(sprint.getTitle(), "UTF-8");
                request += "&" + URLEncoder.encode("usernick", "UTF-8") + "=" + URLEncoder.encode(sprint.getUsernick(), "UTF-8");
                request += "&" + URLEncoder.encode("serverpassword", "UTF-8") + "=" + URLEncoder.encode(MainActivity.SERVERPASSWORD, "UTF-8");

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(request);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                users.clear();

                String readed = reader.readLine();

                while(readed != null)
                {
                    users.add(readed);
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
            ((ProgressBar) getView().findViewById(R.id.fragmentTaskProgressBar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ((ProgressBar) getView().findViewById(R.id.fragmentTaskProgressBar)).setVisibility(View.INVISIBLE);
            if(s.equals("TRUE")){
                showAscribeTask();
            }
            else if(s.equals("FAILED"))
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
        }
    }

    private class claimTask extends AsyncTask<String[], Void, String>
    {
        @Override
        protected String doInBackground(String[]... strings) {
            try {
                URL url = new URL("http://remes.ct8.pl/taskstatuschange.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String[] local = strings[0];

                String request;

                request = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(sprint.getTable_name(), "UTF-8");
                request += "&" + URLEncoder.encode("usernick", "UTF-8") + "=" + URLEncoder.encode(local[1], "UTF-8");
                request += "&" + URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(task.getId()), "UTF-8");
                request += "&" + URLEncoder.encode("state", "UTF-8") + "=" + URLEncoder.encode(local[0], "UTF-8");
                request += "&" + URLEncoder.encode("serverpassword", "UTF-8") + "=" + URLEncoder.encode(MainActivity.SERVERPASSWORD, "UTF-8");

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(request);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String readed = reader.readLine();

                conn.disconnect();

                return readed;
            } catch (Exception e) {
                e.printStackTrace();
                return "FAILED";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((ProgressBar) getView().findViewById(R.id.fragmentTaskProgressBar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ((ProgressBar) getView().findViewById(R.id.fragmentTaskProgressBar)).setVisibility(View.INVISIBLE);

            if(s.equals("FAILED")) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
                return;
            }

            if(!myTasksFragmentOpenedBefore) {
                TasksBoardFragment fragment = new TasksBoardFragment();
                fragment.loaded_flag = true;
                fragment.refresh_flag = true;
                fragment.sprint = sprint;
                fragment.scrollToTask = task;
                fragment.accFilter = -1;
                ((MainActivity) getActivity()).updateFragment(fragment, false);
            }
            else{
                MyTasksFragment fragment = new MyTasksFragment();
                fragment.scrollToTask = task;
                fragment.scrollToSprint = sprint;
                fragment.accFilter = 0;
                ((MainActivity) getActivity()).updateFragment(fragment, false);
            }

        }
    }

    private class deleteTask extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://remes.ct8.pl/deltask.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String request;

                request = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(sprint.getTable_name(), "UTF-8");
                request += "&" + URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(task.getId()), "UTF-8");
                request += "&" + URLEncoder.encode("serverpassword", "UTF-8") + "=" + URLEncoder.encode(MainActivity.SERVERPASSWORD, "UTF-8");

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(request);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String readed = reader.readLine();

                conn.disconnect();

                return readed;
            } catch (Exception e) {
                e.printStackTrace();
                return "FAILED";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((ProgressBar) getView().findViewById(R.id.fragmentTaskProgressBar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ((ProgressBar) getView().findViewById(R.id.fragmentTaskProgressBar)).setVisibility(View.INVISIBLE);
            if(s.equals("FAILED")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
            }
            else
                onBackPressed();
        }
    }

}
