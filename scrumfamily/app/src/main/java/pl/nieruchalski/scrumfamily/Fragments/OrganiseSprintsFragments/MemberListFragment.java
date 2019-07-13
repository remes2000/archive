package pl.nieruchalski.scrumfamily.Fragments.OrganiseSprintsFragments;


import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import pl.nieruchalski.scrumfamily.Fragments.OrganiseSprintsFragment;
import pl.nieruchalski.scrumfamily.Fragments.TaskFragment;
import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberListFragment extends ListFragment {

    private Sprint sprint;

    private String member;

    private int choice = 0;

    ArrayList<String> members;

    public MemberListFragment() {
        // Required empty public constructor
    }

    public void setSprint(Sprint sprint){
        this.sprint = sprint;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        members = new ArrayList<String>();

        if(savedInstanceState!=null){
            members = savedInstanceState.getStringArrayList("members");
            sprint = new Sprint(savedInstanceState.getString("sprinttablename"));
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).accFragment = "MemberListFragment";
        ((MainActivity) getActivity()).memberListFragment = this;
        ((MainActivity) getActivity()).underlineOption();
        ((MainActivity) getActivity()).setTitle(titleToUseless(sprint.getTitle()));
        new getMembers().execute();
    }

    public void onBackPressed(){
        OrganiseSprintsFragment fragment = new OrganiseSprintsFragment();
        fragment.loaded_flag = true;
        fragment.sprint = this.sprint;
        ((MainActivity) getActivity()).updateFragment(fragment, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if(!MainActivity.NICKNAME.equals(sprint.getUsernick()))
            return;

        member = ((TextView) v).getText().toString();
        showChooseAction();
    }

    public void showListView() {
        setListAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, members){
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.BLACK);
                return textView;
            }
        });
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

    private void showChooseAction(){

        CharSequence[] charSequences = new CharSequence[1];

        charSequences[0] = getResources().getString(R.string.kickMember);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.chooseAction))
                .setSingleChoiceItems(charSequences, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        choice = i;
                    }
                })
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(!member.equals(MainActivity.NICKNAME))
                            new leaveSprint().execute(member);
                        else
                            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.youCantKickYourself), Toast.LENGTH_SHORT).show();
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
        outState.putStringArrayList("members", members);
        outState.putString("sprinttablename", sprint.getTable_name());
    }

    private class getMembers extends AsyncTask<Void, Void, String>
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

                members.clear();

                String readed = reader.readLine();

                while(readed != null)
                {
                    members.add(readed);
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
            showListView();
        }
    }

    private class leaveSprint extends AsyncTask<String, Void, String>
    {


        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://remes.ct8.pl/leaveSprint.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String nickToKick = strings[0];

                String request;

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                request = "&" + URLEncoder.encode("usernick", "UTF-8") + "=" + URLEncoder.encode(nickToKick, "UTF-8");
                request += "&" + URLEncoder.encode("sprinttitle", "UTF-8") + "=" + URLEncoder.encode(sprint.getTitle(), "UTF-8");
                request += "&" + URLEncoder.encode("adminNick", "UTF-8") + "=" + URLEncoder.encode(sprint.getUsernick(), "UTF-8");
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
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("TRUE")){;}

            else if(s.equals("FALSE")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
            }

            else
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();

            new getMembers().execute();
        }
    }
}
