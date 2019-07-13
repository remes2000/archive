package pl.nieruchalski.scrumfamily.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.HelpingClasses.Task;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;


public class AddTasksFragment extends Fragment {

    public static final AddTasksFragment ADD_TASKS_FRAGMENT = new AddTasksFragment();

    private Button addTask;
    public EditText title;
    public EditText description;

    private View view;

    private MainActivity mA;

    public AddTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_tasks, container, false);
        getActivity().setTitle(getResources().getString(R.string.nowAddTasks));
        title = (EditText) view.findViewById(R.id.editText1AddTasks);
        description = (EditText) view.findViewById(R.id.editText2AddTasks);
        addTask = (Button) view.findViewById(R.id.buttonAddTasks);
        this.view = view;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).accFragment = "AddTasksFragment";
        ((MainActivity) getActivity()).showOverflowMenu(true);
        ((MainActivity) getActivity()).underlineOption();

        new findSprintsTitles().execute();
        mA = (MainActivity) getActivity();
        if(savedInstanceState!=null) {
            ArrayList<String> titlesArray = savedInstanceState.getStringArrayList("titles");
            ArrayList<String> descriptionsArray = savedInstanceState.getStringArrayList("descriptions");
            for (int i = 0; i < titlesArray.size(); i++) {
                mA.tasks.add(new Task("0",titlesArray.get(i), descriptionsArray.get(i), "0", null));
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(title.getText().toString().isEmpty()){
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.tasksHaveToHaveTitle), Toast.LENGTH_SHORT).show();
                    return;
                }

                mA.tasks.add(new Task("0",title.getText().toString(), description.getText().toString(), "0", null));
                title.setText("");
                description.setText("");
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> titlesArray = new ArrayList<String>();
        ArrayList<String> descriptionsArray = new ArrayList<String>();
        for(int i=0; i<mA.tasks.size(); i++)
        {
            titlesArray.add(mA.tasks.get(i).getTitle());
            descriptionsArray.add(mA.tasks.get(i).getDescription());
        }
        outState.putStringArrayList("titles", titlesArray);
        outState.putStringArrayList("descriptions", descriptionsArray);
    }

    public void showProgressBar()
    {
        (   (ProgressBar) view.findViewById(R.id.addTasksProgressBar) ).setVisibility(View.VISIBLE);
        (   (EditText)  view.findViewById(R.id.editText1AddTasks) ).setEnabled(false);
        (   (EditText)  view.findViewById(R.id.editText2AddTasks) ).setEnabled(false);
        (   (Button)  view.findViewById(R.id.buttonAddTasks) ).setEnabled(false);
    }

    public void hideProgressBar()
    {
        (   (ProgressBar)  view.findViewById(R.id.addTasksProgressBar) ).setVisibility(View.INVISIBLE);
        (   (EditText)  view.findViewById(R.id.editText1AddTasks) ).setEnabled(true);
        (   (EditText)  view.findViewById(R.id.editText2AddTasks) ).setEnabled(true);
        (   (Button)  view.findViewById(R.id.buttonAddTasks) ).setEnabled(true);
    }


    @Override
    public void onPause() {
        super.onPause();
        if(!mA.tasks.isEmpty())
        {
            if(isRemoving())
                showDidntSaveTasksError();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).showOverflowMenu(false);
    }

    public void showDidntSaveTasksError()
    {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(getResources().getString(R.string.didintSaveTasksError))
                .setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mA.showSaveAlertDialog();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mA.tasks.clear();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showSprintsError()
    {
        EmptyFragment fragment = new EmptyFragment();
        fragment.setFragmentOptions(true, getResources().getString(R.string.newSprint), getResources().getString(R.string.youtDontHaveAnySprints), new NewSprintTitleFragment());
        ((MainActivity)getActivity()).updateFragment(fragment, false);
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

                mA.avaliableSprints.clear();

                String readed = reader.readLine();

                while(readed != null)
                {
                    if(readed.equals("@FALSE@"))
                        return readed;

                    mA.avaliableSprints.add(new Sprint(readed, reader.readLine()));
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
            showProgressBar();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideProgressBar();
            if(s.equals("@FALSE@")){
                showSprintsError();
            }

        }
    }

}
