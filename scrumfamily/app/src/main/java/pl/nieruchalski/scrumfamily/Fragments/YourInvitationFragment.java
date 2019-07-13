package pl.nieruchalski.scrumfamily.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

import pl.nieruchalski.scrumfamily.Adapters.InvitationAdapter;
import pl.nieruchalski.scrumfamily.HelpingClasses.Invitation;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourInvitationFragment extends Fragment {

    private ListView listView;
    public ArrayList<Invitation> invitations;

    public YourInvitationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        invitations = new ArrayList<Invitation>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_your_invitation, container, false);
        getActivity().setTitle(getResources().getString(R.string.yourInvites));

        listView = (ListView) view.findViewById(R.id.invitationList);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).accFragment = "YourInvitationFragment";
        ((MainActivity) getActivity()).showOverflowMenuRefreshInvitation(true);
        ((MainActivity) getActivity()).underlineOption();

        new getInvitations().execute();
    }

    public void getInvitations()
    {
        invitations.clear();
        new getInvitations().execute();
    }

    public void showProgressBar()
    {
        ((ProgressBar) getView().findViewById(R.id.yourInvitationsProgressBar)).setVisibility(View.VISIBLE);
    }

    public void hideProgressBar()
    {
        ((ProgressBar) getView().findViewById(R.id.yourInvitationsProgressBar)).setVisibility(View.INVISIBLE);
    }

    public void showInvitationsError(){
        EmptyFragment fragment = new EmptyFragment();
        fragment.setFragmentOptions(true, getResources().getString(R.string.refresh), getResources().getString(R.string.youdidntgetanyinvitations), new YourInvitationFragment());
        ((MainActivity)getActivity()).updateFragment(fragment, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).showOverflowMenuRefreshInvitation(false);
    }

    private class getInvitations extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://remes.ct8.pl/getInvitations.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                String request;

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                request = URLEncoder.encode("usernick", "UTF-8") + "=" + URLEncoder.encode(MainActivity.NICKNAME, "UTF-8");
                request += "&" + URLEncoder.encode("serverpassword", "UTF-8") + "=" + URLEncoder.encode(MainActivity.SERVERPASSWORD, "UTF-8");

                writer.write(request);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String readed;

                readed = reader.readLine();

                if(readed.equals("FALSE"))
                    return readed;

                while(readed!=null)
                {
                    invitations.add(new Invitation(reader.readLine(), readed));
                    readed = reader.readLine();
                }

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
            ((ProgressBar) getView().findViewById(R.id.yourInvitationsProgressBar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ((ProgressBar) getView().findViewById(R.id.yourInvitationsProgressBar)).setVisibility(View.INVISIBLE);

            if(s == null){
                listView.setAdapter(new InvitationAdapter(getActivity().getApplicationContext(), invitations, YourInvitationFragment.this));
            }

            else if(s.equals("FALSE")){
                showInvitationsError();
            }

            else if(s.equals("FAILED")) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
