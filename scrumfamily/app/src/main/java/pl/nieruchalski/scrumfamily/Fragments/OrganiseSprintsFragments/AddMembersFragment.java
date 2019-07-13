package pl.nieruchalski.scrumfamily.Fragments.OrganiseSprintsFragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
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
import java.nio.charset.Charset;

import pl.nieruchalski.scrumfamily.Fragments.OrganiseSprintsFragment;
import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMembersFragment extends Fragment {

    private Sprint sprint;
    private EditText etSendTo;
    private Button btnSendTo;

    public AddMembersFragment() {
        // Required empty public constructor
    }

    public void setSprint(Sprint s){this.sprint = s;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_members, container, false);

        if(savedInstanceState!=null)
            sprint = new Sprint(savedInstanceState.getString("usernick"), savedInstanceState.getString("title"));

        etSendTo = (EditText) view.findViewById(R.id.eTNickAddMembers);
        btnSendTo = (Button) view.findViewById(R.id.buttonAddMembers);

        btnSendTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String st = etSendTo.getText().toString();
                if(st.isEmpty())
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.completeAllBlanks), Toast.LENGTH_SHORT).show();
                else if(st.trim().equals(MainActivity.NICKNAME))
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.cannotaddyourself), Toast.LENGTH_SHORT).show();
                else
                    new sendInvitation().execute(st);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(titleToUseless(sprint.getTitle()));
        ((MainActivity) getActivity()).accFragment = "AddMembersFragment";
        ((MainActivity) getActivity()).addMembersFragment = this;
        ((MainActivity) getActivity()).underlineOption();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("usernick", sprint.getUsernick());
        outState.putString("title", sprint.getTitle());
    }

    public String titleToUseless(String titile)
    {
        StringBuilder builder = new StringBuilder(titile);
        for(int i=0; i<titile.length(); i++){
            if(titile.charAt(i) == '_')
                builder.setCharAt(i, ' ');
        }
        return builder.toString();
    }

    public void onBackPressed(){
        OrganiseSprintsFragment fragment = new OrganiseSprintsFragment();
        fragment.loaded_flag = true;
        fragment.sprint = this.sprint;
        ((MainActivity) getActivity()).updateFragment(fragment, false);
    }

    private class sendInvitation extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://remes.ct8.pl/sendInvitation.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String local = strings[0];

                String request;

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                request = URLEncoder.encode("usernick", "UTF-8") + "=" + URLEncoder.encode(sprint.getUsernick(), "UTF-8");
                request += "&" + URLEncoder.encode("sprinttitle", "UTF-8") + "=" + URLEncoder.encode(sprint.getTitle(), "UTF-8");
                request += "&" + URLEncoder.encode("sendto", "UTF-8") + "=" + URLEncoder.encode(local, "UTF-8");
                request += "&" + URLEncoder.encode("serverpassword", "UTF-8") + "=" + URLEncoder.encode(MainActivity.SERVERPASSWORD, "UTF-8");

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
        protected void onPreExecute() {
            super.onPreExecute();
            ((ProgressBar) getView().findViewById(R.id.addMembersProgressBar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ((ProgressBar) getView().findViewById(R.id.addMembersProgressBar)).setVisibility(View.INVISIBLE);

            if(s.equals("TRUE")) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.invitationSent), Toast.LENGTH_SHORT).show();
                etSendTo.setText("");
            }

            else if(s.equals("FALSE"))
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.userDoesntExsist), Toast.LENGTH_SHORT).show();

            else if(s.equals("FAILED"))
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();

        }
    }
}
