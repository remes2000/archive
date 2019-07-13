package pl.nieruchalski.scrumfamily.Fragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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
import java.sql.Time;
import java.util.Calendar;

import pl.nieruchalski.scrumfamily.Database.SprintsDatabaseHelper;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

public class NewSprintFragment extends Fragment {

    public NewSprintFragment() {
        // Required empty public constructor
    }

    DatePicker datePicker;
    Button button;

    String sprintTitle;

    int yearStart;
    int monthStart;
    int dayStart;

    int yearStop;
    int monthStop;
    int dayStop;

    public void setSprintTitle(String title) {this.sprintTitle = title;}

    private String toUselessStartDate(Calendar date)
    {
        String dateRet = "";
        dateRet = Integer.toString(date.get(Calendar.YEAR));
        dateRet = dateRet + "-" + Integer.toString(date.get(Calendar.MONTH) + 1);
        dateRet = dateRet + "-" + Integer.toString(date.get(Calendar.DAY_OF_MONTH));
        return dateRet;
    }

    private String toUselessEndDate(Calendar date)
    {
        String dateRet = "";
        dateRet = Integer.toString(date.get(Calendar.YEAR));
        dateRet = dateRet + "-" + Integer.toString(date.get(Calendar.MONTH) + 1);
        dateRet = dateRet + "-" + Integer.toString(date.get(Calendar.DAY_OF_MONTH));
        return dateRet;
    }

    private String titleToUseless(String title)
    {
        StringBuilder builder = new StringBuilder(title);
        int l = title.length();
        for(int i=0; i<l; i++)
        {
            if(title.charAt(i) == ' ')
                builder.setCharAt(i, '_');
        }
        return builder.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_sprint, container, false);

        getActivity().setTitle(getResources().getString(R.string.whenSprintStarts));

        datePicker = (DatePicker) view.findViewById(R.id.dataPicker);

        button = (Button) view.findViewById(R.id.nextButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                yearStart = datePicker.getYear();
                monthStart = datePicker.getMonth();
                dayStart = datePicker.getDayOfMonth();
                Calendar calendar = Calendar.getInstance();
                calendar.set(yearStart, monthStart, dayStart);

                if(calendar.before(now))
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.wrongInputv2), Toast.LENGTH_SHORT).show();
                else {

                    getActivity().setTitle(getResources().getString(R.string.whenSprintEnds));
                    button.setText(getResources().getString(R.string.goNextv2));


                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            yearStop = datePicker.getYear();
                            monthStop = datePicker.getMonth();
                            dayStop = datePicker.getDayOfMonth();
                            Calendar a = Calendar.getInstance();
                            a.set(yearStart, monthStart, dayStart);
                            Calendar b = Calendar.getInstance();
                            b.set(yearStop, monthStop, dayStop);
                            if ((b.before(a)))  // ERROR WRONG INPUT
                                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.wrongInput), Toast.LENGTH_SHORT).show();
                            else {
                                new createNewSprint().execute(new String[]{titleToUseless(sprintTitle), MainActivity.NICKNAME, toUselessStartDate(a), toUselessEndDate(b), LogInFragment.ET_LOG_STATE});
                            }
                        }
                    });
                }

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).accFragment = "NewSprintFragment";
        ((MainActivity) getActivity()).underlineOption();
    }

    private class createNewSprint extends AsyncTask<String[], Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            (   (ProgressBar)   getView().findViewById(R.id.addSprintProgressBar)).setVisibility(View.VISIBLE);
            (   (DatePicker)    getView().findViewById(R.id.dataPicker)).setEnabled(false);
            (   (Button)    getView().findViewById(R.id.nextButton)).setEnabled(false);
        }

        @Override
        protected String doInBackground(String[]... strings) {

            String local[] = strings[0];

            try {
                URL url = new URL("http://remes.ct8.pl/newsprint.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String request;

                request = URLEncoder.encode("sprinttitle", "UTF-8") + "=" + URLEncoder.encode(local[0], "UTF-8");
                request += "&" + URLEncoder.encode("usernick", "UTF-8") + "=" + URLEncoder.encode(local[1], "UTF-8");
                request += "&" + URLEncoder.encode("startdate", "UTF-8") + "=" + URLEncoder.encode(local[2], "UTF-8");
                request += "&" + URLEncoder.encode("enddate", "UTF-8") + "=" + URLEncoder.encode(local[3], "UTF-8");
                request += "&" + URLEncoder.encode("usermail", "UTF-8") + "=" + URLEncoder.encode(local[4], "UTF-8");
                request += "&" + URLEncoder.encode("serverpassword", "UTF-8") + "=" + URLEncoder.encode(MainActivity.SERVERPASSWORD, "UTF-8");

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(request);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String readed;

                readed = reader.readLine();

                conn.disconnect();

                return readed;
            } catch (Exception e) {
                e.printStackTrace();
                return "FAILED";
            }
        }


        @Override
        protected void onPostExecute(String b) {
            (   (ProgressBar)   getView().findViewById(R.id.addSprintProgressBar)).setVisibility(View.INVISIBLE);
            (   (DatePicker)    getView().findViewById(R.id.dataPicker)).setEnabled(true);
            (   (Button)    getView().findViewById(R.id.nextButton)).setEnabled(true);
            if(b.equals("TRUE")) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.sprintSuccesfullAdded), Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).updateFragment(AddTasksFragment.ADD_TASKS_FRAGMENT, false);
            }
            else if(b.equals("FALSE"))
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.sprintFalse), Toast.LENGTH_SHORT).show();
            else if(b.equals("FAILED"))
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
        }

    }

}
