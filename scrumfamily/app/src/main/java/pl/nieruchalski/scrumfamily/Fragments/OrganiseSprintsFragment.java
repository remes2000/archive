package pl.nieruchalski.scrumfamily.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import pl.nieruchalski.scrumfamily.Adapters.CardViewOrgainseSprintsAdapter;
import pl.nieruchalski.scrumfamily.Fragments.OrganiseSprintsFragments.AddMembersFragment;
import pl.nieruchalski.scrumfamily.Fragments.OrganiseSprintsFragments.MemberListFragment;
import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrganiseSprintsFragment extends Fragment {

    private int choice = 0;
    public Sprint sprint;

    public Boolean loaded_flag = false;

    private RecyclerView optionsList;
    private ArrayList<Sprint> sprints;
    private ArrayList<String> titles;

    private ArrayList<String> options;
    private ArrayList<Integer> imageResources;

    public OrganiseSprintsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organise_sprints, container, false);
        optionsList = (RecyclerView) view.findViewById(R.id.optionsList);
        sprints = new ArrayList<Sprint>();
        titles = new ArrayList<String>();
        options = new ArrayList<String>();
        imageResources = new ArrayList<Integer>();

        if(savedInstanceState!=null){
            loaded_flag = savedInstanceState.getBoolean("loaded_flag");
            sprint = new Sprint(savedInstanceState.getString("sprintTableName"));
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.organiseSprints));

        ((MainActivity) getActivity()).accFragment = "OrganiseSprintsFragment";
        ((MainActivity) getActivity()).underlineOption();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        optionsList.setLayoutManager(layoutManager);

        if(sprint != null)
            ((MainActivity) getActivity()).setTitle(titleToUseless(sprint.getTitle()));

        if(!loaded_flag)
            new findSprints().execute();
        else
            showOptions();
    }

    public String titleToUseless(String title)
    {
        int l = title.length();
        StringBuilder builder = new StringBuilder(title);
        for(int i=0; i<l; i++){
            if(title.charAt(i) == '_')
                builder.setCharAt(i, ' ');
        }
        return builder.toString();
    }

    private void showOptions(){

        if(sprint.getUsernick().equals(MainActivity.NICKNAME)){
            options.add(getResources().getString(R.string.addMembers));
            options.add(getResources().getString(R.string.manageSprintMembers));
            options.add(getResources().getString(R.string.endAndSummarySprint));
            options.add(getResources().getString(R.string.deleteSprint));

            imageResources.add(R.mipmap.add_members_icon);
            imageResources.add(R.mipmap.ic_managetask);
            imageResources.add(R.mipmap.end_sprint_icon);
            imageResources.add(R.mipmap.trash_icon);
        }
        else{
            options.add(getResources().getString(R.string.showSprintMembers));
            options.add(getResources().getString(R.string.leaveSprint));

            imageResources.add(R.mipmap.show_members_icon);
            imageResources.add(R.mipmap.leave_sprint_icon);
        }

        optionsList.setAdapter(new CardViewOrgainseSprintsAdapter(options, imageResources, this));
    }

    public void onOptionClicked(int position){

        if(sprint.getUsernick().equals(MainActivity.NICKNAME))
            onAdminOptionClicked(position);
        else
            onUserOptionClicked(position);

    }

    private void onAdminOptionClicked(int position){
        switch (position){
            case 0:
                AddMembersFragment fragment = new AddMembersFragment();
                fragment.setSprint(sprint);
                ((MainActivity)getActivity()).updateFragment(fragment, false);
                break;
            case 1:
                MemberListFragment fragment1 = new MemberListFragment();
                fragment1.setSprint(sprint);
                ((MainActivity)getActivity()).updateFragment(fragment1, false);
                break;
            case 2:
                showDoYouWantEndSprintAlertDialog();
                break;
            case 3:
                showDoYouWantDeleteAlertDialog();
                break;
        }
    }

    private void onUserOptionClicked(int position){
        switch (position){
            case 0:
                MemberListFragment fragment1 = new MemberListFragment();
                fragment1.setSprint(sprint);
                ((MainActivity)getActivity()).updateFragment(fragment1, false);
                break;
            case 1:
                showDoYouWantLeave();
                break;
        }
    }

    private void showDoYouWantEndSprintAlertDialog(){
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.doYouWantEndSprint))
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        new endSprint().execute();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create();
        dialog.show();
    }

    private void showDoYouWantDeleteAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.doYouWantDeleteSprints))
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        new deleteSprint().execute();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create();
        dialog.show();
    }

    private void showDoYouWantLeave()
    {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.doYouWantLeaveSprint))
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        new leaveSprint().execute();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create();
        dialog.show();
    }

    private void showSprintsError()
    {
        EmptyFragment fragment = new EmptyFragment();
        fragment.setFragmentOptions(true, getResources().getString(R.string.newSprint), getResources().getString(R.string.youtDontHaveAnySprints), new NewSprintTitleFragment());
        ((MainActivity)getActivity()).updateFragment(fragment, false);
    }

    private void showChoiceDialog(){
        CharSequence[] charSequences = new CharSequence[sprints.size()];

        for(int i=0; i<sprints.size(); i++)
            charSequences[i] = titleToUseless(sprints.get(i).getTitle());

        choice = 0;

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.whichOneOpen))
                .setSingleChoiceItems(charSequences, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        choice = i;
                    }
                })
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sprint = sprints.get(choice);
                        showOptions();
                        ((MainActivity)getActivity()).setTitle(titleToUseless(sprint.getTitle()));
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("loaded_flag", loaded_flag);
        if(sprint != null)
            outState.putString("sprintTableName", sprint.getTable_name());
    }

    private class findSprints extends AsyncTask<Void, Void, String>
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

                sprints.clear();

                String readed = reader.readLine();

                while(readed != null)
                {
                    if(readed.equals("@FALSE@"))
                        return readed;

                    sprints.add(new Sprint(readed, reader.readLine()));
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
            ((ProgressBar) getView().findViewById(R.id.progressBarOrganiseSprints)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ((ProgressBar) getView().findViewById(R.id.progressBarOrganiseSprints)).setVisibility(View.INVISIBLE);
            if(s.equals("@FALSE@")){
                showSprintsError();
            }
            else if(s.equals("FAILED")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
            }
            else{

                if(sprints.size() > 1)
                    showChoiceDialog();
                else{
                    sprint = sprints.get(0);
                    ((MainActivity) getActivity()).setTitle(titleToUseless(sprint.getTitle()));
                    showOptions();
                }

                loaded_flag = true;

            }

        }
    }

    private class deleteSprint extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://remes.ct8.pl/delsprint.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String request;

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                request = URLEncoder.encode("usernick", "UTF-8") + "=" + URLEncoder.encode(sprint.getUsernick(), "UTF-8");
                request += "&" + URLEncoder.encode("sprinttitle", "UTF-8") + "=" + URLEncoder.encode(sprint.getTitle(), "UTF-8");
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
            ((ProgressBar) getView().findViewById(R.id.progressBarOrganiseSprints)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ((ProgressBar) getView().findViewById(R.id.progressBarOrganiseSprints)).setVisibility(View.INVISIBLE);

            if(s.equals("TRUE")) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.succesfullDelete), Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).updateFragment(new OrganiseSprintsFragment(), false);
            }

            else if(s.equals("FALSE"))
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

            else if(s.equals("FAILED"))
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
        }
    }

    private class leaveSprint extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://remes.ct8.pl/leaveSprint.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String request;

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                request = "&" + URLEncoder.encode("usernick", "UTF-8") + "=" + URLEncoder.encode(MainActivity.NICKNAME, "UTF-8");
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
            ((ProgressBar) getView().findViewById(R.id.progressBarOrganiseSprints)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ((ProgressBar) getView().findViewById(R.id.progressBarOrganiseSprints)).setVisibility(View.INVISIBLE);

            if(s.equals("TRUE")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.youLeftSprint), Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).updateFragment(new OrganiseSprintsFragment(), false);
            }

            else if(s.equals("FALSE")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
            }

            else
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();

        }
    }

    private class endSprint extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://remes.ct8.pl/endsprint.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String request;

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                request = URLEncoder.encode("usernick", "UTF-8") + "=" + URLEncoder.encode(sprint.getUsernick(), "UTF-8");
                request += "&" + URLEncoder.encode("sprinttitle", "UTF-8") + "=" + URLEncoder.encode(sprint.getTitle(), "UTF-8");
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
            ((ProgressBar) getView().findViewById(R.id.progressBarOrganiseSprints)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ((ProgressBar) getView().findViewById(R.id.progressBarOrganiseSprints)).setVisibility(View.INVISIBLE);

            if(s == null){
                SprintSummaryFragment fragment = new SprintSummaryFragment();
                ((MainActivity) getActivity()).updateFragment(fragment, false);
                return;
            }

            else if(s.equals("FAILED")) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

}
