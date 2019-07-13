package pl.nieruchalski.scrumfamily.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewSprintTitleFragment extends Fragment {

    private MainActivity mA;

    Button goNext;
    EditText sprintTitle;


    public NewSprintTitleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_sprint_tiltle, container, false);

        getActivity().setTitle(getResources().getString(R.string.setSprintTitle));

        goNext = (Button) view.findViewById(R.id.buttonGoNextSprintTitle);
        sprintTitle = (EditText) view.findViewById(R.id.etSprintTitle);

        goNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((!sprintTitle.getText().toString().isEmpty())) {
                    new isTitleExist().execute(new String[]{MainActivity.NICKNAME, titleToUseless(sprintTitle.getText().toString()), MainActivity.SERVERPASSWORD});
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).accFragment = "NewSprintTitleFragment";
        ((MainActivity) getActivity()).underlineOption();

        mA = (MainActivity) getActivity();
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

    private class isTitleExist extends AsyncTask<String[], Void, String>
    {

        @Override
        protected String doInBackground(String[]... strings) {
            try {
                URL url = new URL("http://remes.ct8.pl/titlecheck.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String local[] = strings[0];

                String request;

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                request = URLEncoder.encode("usernick", "UTF-8") + "=" + URLEncoder.encode(local[0], "UTF-8");
                request += "&" + URLEncoder.encode("sprinttitle", "UTF-8") + "=" + URLEncoder.encode(local[1], "UTF-8");
                request += "&" + URLEncoder.encode("serverpassword", "UTF-8") + "=" + URLEncoder.encode(MainActivity.SERVERPASSWORD, "UTF-8");

                writer.write(request);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String readed;

                readed = reader.readLine();

                return readed;
            } catch (Exception e) {
                e.printStackTrace();
                return "FAILED";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((ProgressBar) getView().findViewById(R.id.newSprintTitleProgressBar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ((ProgressBar) getView().findViewById(R.id.newSprintTitleProgressBar)).setVisibility(View.INVISIBLE);
            if(s.equals("TRUE")) {
                NewSprintFragment fragment = new NewSprintFragment();
                fragment.setSprintTitle(sprintTitle.getText().toString().trim());
                mA.updateFragment(fragment, false);
            }
            else if(s.equals("FALSE")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.sprintFalse ), Toast.LENGTH_SHORT).show();
            }
            else if(s.equals("FAILED")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
