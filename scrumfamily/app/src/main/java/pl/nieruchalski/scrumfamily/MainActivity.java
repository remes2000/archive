package pl.nieruchalski.scrumfamily;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import pl.nieruchalski.scrumfamily.Adapters.CustomAdapter;
import pl.nieruchalski.scrumfamily.Database.SprintsDatabaseHelper;
import pl.nieruchalski.scrumfamily.Fragments.AddTasksFragment;
import pl.nieruchalski.scrumfamily.Fragments.LogInFragment;
import pl.nieruchalski.scrumfamily.Fragments.MyTasksFragment;
import pl.nieruchalski.scrumfamily.Fragments.NewSprintTitleFragment;
import pl.nieruchalski.scrumfamily.Fragments.OrganiseSprintsFragment;
import pl.nieruchalski.scrumfamily.Fragments.OrganiseSprintsFragments.AddMembersFragment;
import pl.nieruchalski.scrumfamily.Fragments.OrganiseSprintsFragments.MemberListFragment;
import pl.nieruchalski.scrumfamily.Fragments.SprintSummaryDataFragment;
import pl.nieruchalski.scrumfamily.Fragments.SprintSummaryFragment;
import pl.nieruchalski.scrumfamily.Fragments.SprintSummaryTaskBoard.SprintSummaryTask;
import pl.nieruchalski.scrumfamily.Fragments.SprintSummaryTaskBoard.SprintSummaryTaskBoard;
import pl.nieruchalski.scrumfamily.Fragments.TaskFragment;
import pl.nieruchalski.scrumfamily.Fragments.TasksBoardFragment;
import pl.nieruchalski.scrumfamily.Fragments.YourInvitationFragment;
import pl.nieruchalski.scrumfamily.HelpingClasses.Sprint;
import pl.nieruchalski.scrumfamily.HelpingClasses.Task;

public class MainActivity extends Activity {

    //Created by Michal Nieruchalski & Mikolaj Bartela (webservice)

    public static boolean LOGGED_IN = false;
    public static boolean OFFLINE_MODE = false;
    public static String NICKNAME = "";
    public static String SERVERPASSWORD = "e34ae4e1ab84a96307eaecedb501ad73a203fc8345ecb6a78074ce48515e90f07be99c8b32e36f202dd3c909af8d7caf14fb0fb7201a7292be7cdfabbd6806ea";
    public ArrayList<Sprint> avaliableSprints;
    public ArrayList<Task> tasks;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private Menu menu;

    private Bundle SavedInstanceStateB;
    public static Boolean isEmptyErrorOnForegroundFlag = false;

    public TasksBoardFragment tasksBoardFragment;
    public TaskFragment taskFragment;
    public MyTasksFragment myTasksFragment;
    public AddMembersFragment addMembersFragment;
    public MemberListFragment memberListFragment;
    public SprintSummaryFragment sprintSummaryFragment;
    public SprintSummaryDataFragment sprintSummaryDataFragment;
    public SprintSummaryTaskBoard sprintSummaryTaskBoard;
    public SprintSummaryTask sprintSummaryTask;

    public String accFragment = "LogInFragment";

    public int[] options = {R.string.logIn, R.string.newSprint, R.string.taskBlackboard,R.string.myTasks, R.string.addTasks, R.string.organiseSprints, R.string.yourInvites, R.string.sprintSummary};
    public int[] images = {R.mipmap.ic_login,R.mipmap.newsprint, R.mipmap.ic_blackboard,R.mipmap.ic_mytasksboard, R.mipmap.ic_add_tasks, R.mipmap.ic_organise, R.mipmap.ic_invite, R.mipmap.ic_sprintsummary};
    public int[] imagesWhite = {R.mipmap.ic_login_white, R.mipmap.newsprint_white, R.mipmap.ic_blackboard_white,R.mipmap.ic_mytasksboard_white, R.mipmap.ic_add_tasks_white, R.mipmap.ic_organise_white, R.mipmap.ic_invite_white, R.mipmap.ic_sprintsummary_white};
    public int[] imagesBlack = {R.mipmap.ic_login,R.mipmap.newsprint, R.mipmap.ic_blackboard, R.mipmap.ic_mytasksboard, R.mipmap.ic_add_tasks, R.mipmap.ic_organise, R.mipmap.ic_invite, R.mipmap.ic_sprintsummary};
    public int[] textColors = {Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY};
    public int[] backgroundColors = {Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY};
    public ArrayList<String> optionsString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState==null){
            if(NICKNAME.equals("@FAILED@")){;}
            else if(NICKNAME.equals("@EMPTY@")){;}
            else
                Toast.makeText(this, getResources().getString(R.string.welcome) + " " + NICKNAME, Toast.LENGTH_SHORT).show();

            SQLiteOpenHelper helper = new SprintsDatabaseHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query("MYTASKS", null, null, null, null, null, null);

            if(OFFLINE_MODE)
                Toast.makeText(this, getResources().getString(R.string.youAreInOfflineMode), Toast.LENGTH_SHORT).show();

            if((OFFLINE_MODE)&&(cursor.moveToFirst()))
                updateFragment(new MyTasksFragment(), false);
            else
                updateFragment(new LogInFragment(), false);

            cursor.close();
            db.close();
        }

        if(savedInstanceState!=null)
            accFragment = savedInstanceState.getString("accFragment");

        tasks = new ArrayList<Task>();
        SavedInstanceStateB = savedInstanceState;

        avaliableSprints = new ArrayList<Sprint>();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerList = (ListView) findViewById(R.id.listView);

        optionsString = new ArrayList<String>();
        for(int o : options)
        {
            optionsString.add(getResources().getString(o));
        }
        underlineOption();
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener()
        {
            Fragment fragment;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i)
                {
                    case 0:
                        fragment = new LogInFragment();
                        break;
                    case 1: // new Sprint
                        if(LOGGED_IN){
                        fragment = new NewSprintTitleFragment();}
                        else if(OFFLINE_MODE)
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.optionAvaliableOnlyInOnlineMode), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.logIntoUseApplication), Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        if(LOGGED_IN){
                        fragment = new TasksBoardFragment();}
                        else if(OFFLINE_MODE)
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.optionAvaliableOnlyInOnlineMode), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.logIntoUseApplication), Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        if(LOGGED_IN){
                            fragment = new MyTasksFragment();}
                        else if(OFFLINE_MODE)
                            fragment = new MyTasksFragment();
                        else
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.logIntoUseApplication), Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        if(LOGGED_IN){
                            fragment = AddTasksFragment.ADD_TASKS_FRAGMENT;}
                        else if(OFFLINE_MODE)
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.optionAvaliableOnlyInOnlineMode), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.logIntoUseApplication), Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        if(LOGGED_IN){
                            fragment = new OrganiseSprintsFragment();
                        }
                        else if(OFFLINE_MODE)
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.optionAvaliableOnlyInOnlineMode), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.logIntoUseApplication), Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        if(LOGGED_IN){
                            fragment = new YourInvitationFragment();
                        }
                        else if(OFFLINE_MODE)
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.optionAvaliableOnlyInOnlineMode), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.logIntoUseApplication), Toast.LENGTH_SHORT).show();
                        break;
                    case 7:
                        if(LOGGED_IN){
                            fragment = new SprintSummaryFragment();
                        }
                        else if(OFFLINE_MODE)
                            fragment = new SprintSummaryFragment();
                        else
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.logIntoUseApplication), Toast.LENGTH_SHORT).show();
                        break;

                }

                if(fragment != null)
                    updateFragment(fragment, false);

                drawerLayout.closeDrawer(drawerList);
            }
        });
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.addDrawerListener(drawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    public void underlineOption(){

        if(drawerList == null)
            return;

        int i = 0;

        if(accFragment.equals("AddTasksFragment"))
            i=4;
        else if(accFragment.equals("LogInFragment"))
            i=0;
        else if(accFragment.equals("NewSprintFragment"))
            i=1;
        else if(accFragment.equals("NewSprintTitleFragment"))
            i=1;
        else if(accFragment.equals("OrganiseSprintsFragment"))
            i=5;
        else if(accFragment.equals("RegistrationFragment"))
            i=0;
        else if(accFragment.equals("TasksBoardFragment"))
            i=2;
        else if(accFragment.equals("YourInvitationFragment"))
            i=6;
        else if(accFragment.equals("AddMembersFragment"))
            i=5;
        else if(accFragment.equals("TaskFragment"))
            i=2;
        else if(accFragment.equals("MyTasksFragment"))
            i=3;
        else if(accFragment.equals("SprintSummaryFragment"))
            i=7;
        else if(accFragment.equals("MemberListFragment"))
            i=5;
        else if(accFragment.equals("SprintSummaryDataFragment"))
            i=7;
        else if(accFragment.equals("SprintSummaryTaskBoard"))
            i=2;
        else if(accFragment.equals("SprintSummaryTask"))
            i=2;


        for(int j=0; j<images.length; j++)
        {
            if(j==i){
                images[j] = imagesWhite[j];
                textColors[j] = Color.WHITE;
                backgroundColors[j] = Color.GRAY;
            }
            else{
                images[j] = imagesBlack[j];
                textColors[j] = Color.GRAY;
                backgroundColors[j] = Color.WHITE;
            }
        }

        drawerList.setAdapter(new CustomAdapter(this, images, optionsString, textColors, backgroundColors));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("accFragment", accFragment);
    }

    public void showOverflowMenu(boolean showMenu){
        if(menu == null)
            return;
        menu.setGroupVisible(R.id.actionbarGroup, showMenu);
    }

    public void showOverflowMenuRefresh(boolean showMenu)
    {
        if(menu==null)
            return;
        menu.setGroupVisible(R.id.actionbarGroupRefresh, showMenu);
    }

    public void showOverflowMenuRefreshInvitation(boolean showMenu)
    {
        if(menu==null)
            return;
        menu.setGroupVisible(R.id.actionbarGroupRefreshInvitation, showMenu);
    }

    public void showOverflowMenuRefreshYourTasks(boolean showMenu)
    {
        if(menu==null)
            return;
        menu.setGroupVisible(R.id.actionbarGroupRefreshYourTasks, showMenu);
    }

    public void showOverflowMenuManageTask(boolean showMenu)
    {
        if(menu==null)
            return;
        menu.setGroupVisible(R.id.actionBarGroupManageTask, showMenu);
    }

    public void showOverflowMenuRefreshSummaries(boolean showMenu){
        if(menu==null)
            return;
        menu.setGroupVisible(R.id.actionBarGroupRefreshSummaries, showMenu);
    }

    public void showOverflowMenuDeleteSummary(boolean showMenu){
        if(menu==null)
            return;
        menu.setGroupVisible(R.id.actionBarGroupDeleteSummary, showMenu);
    }

    public void updateFragment(Fragment fragment, boolean addToBackStack)
    {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, fragment);

        if(addToBackStack)
            ft.addToBackStack(null);

        ft.commit();
        underlineOption();
    }

    public void showMenuSpecialy(Bundle savedInstanceState)
    {
        if(savedInstanceState != null)
        {
            accFragment = savedInstanceState.getString("accFragment");

            if(isEmptyErrorOnForegroundFlag)
                return;

            if(accFragment.equals("AddTasksFragment")){
                showOverflowMenu(true);
            }
            else if (accFragment.equals("TasksBoardFragment")){
                showOverflowMenuRefresh(true);
            }
            else if(accFragment.equals("YourInvitationFragment")){
                showOverflowMenuRefreshInvitation(true);
            }
            else if(accFragment.equals("MyTasksFragment")){
                showOverflowMenuRefreshYourTasks(true);
            }
            else if(accFragment.equals("TaskFragment")){
                showOverflowMenuManageTask(true);
            }
            else if(accFragment.equals("SprintSummaryFragment"))
                showOverflowMenuRefreshSummaries(true);
            else if(accFragment.equals("SprintSummaryDataFragment")){
             if(sprintSummaryDataFragment.saved == true)
                 showOverflowMenuDeleteSummary(true);
            }

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if(accFragment.equals("TaskFragment")){
            taskFragment.onBackPressed();
        }
        else if(accFragment.equals("AddMembersFragment")){
            addMembersFragment.onBackPressed();
        }
        else if(accFragment.equals("MemberListFragment")){
            memberListFragment.onBackPressed();
        }
        else if(accFragment.equals("SprintSummaryDataFragment")){
            sprintSummaryDataFragment.onBackPressed();
        }
        else if(accFragment.equals("SprintSummaryTaskBoard")){
            sprintSummaryTaskBoard.onBackPressed();
        }
        else if(accFragment.equals("SprintSummaryTask"))
            sprintSummaryTask.onBackPressed();
        else
            super.onBackPressed();
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

    public Boolean showSaveAlertDialog()
    {

        AddTasksFragment addTasks = AddTasksFragment.ADD_TASKS_FRAGMENT;

        // to nadole ma się wykonać gdy tytuł jest pusty a opis nie

        if( (addTasks.title.getText().toString().isEmpty()) && (!addTasks.description.getText().toString().isEmpty()))
        {
            Toast.makeText(this, getResources().getString(R.string.tasksHaveToHaveTitle), Toast.LENGTH_SHORT).show();
            return true;
        }

        //jezeli tytuł nie jest pusty

        if(!AddTasksFragment.ADD_TASKS_FRAGMENT.title.getText().toString().isEmpty()){
            tasks.add(new Task("0",AddTasksFragment.ADD_TASKS_FRAGMENT.title.getText().toString(),
                    AddTasksFragment.ADD_TASKS_FRAGMENT.description.getText().toString(),
                    "0", null));
            AddTasksFragment.ADD_TASKS_FRAGMENT.title.setText("");
            AddTasksFragment.ADD_TASKS_FRAGMENT.description.setText("");
        }

        if(tasks.size() == 0){
            Toast.makeText(this, getResources().getString(R.string.youHaveToAddTasks), Toast.LENGTH_SHORT).show();
            return true;
        }

        int arrayLenght = avaliableSprints.size();

        if(arrayLenght == 1){
            new sendTasks(true, true).execute(avaliableSprints.get(0).getTable_name());
            return true;
        }

        CharSequence[] avaliableSprintsArray = new CharSequence[arrayLenght];

        for(int i=0; i<arrayLenght; i++)
            avaliableSprintsArray[i] = titleToUseless(avaliableSprints.get(i).getTitle());

        final ArrayList<Integer> selectedItems = new ArrayList<Integer>();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.whereSaveTasks))
                .setMultiChoiceItems(avaliableSprintsArray, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if(b){
                            selectedItems.add(i);
                        }
                        else{
                            selectedItems.remove(Integer.valueOf(i));
                        }
                    }
                })
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for(int i=0; i<selectedItems.size(); i++)
                        {
                            Boolean isFirst = false;
                            Boolean isLast = false;
                            if(i==0)
                                isFirst = true;
                            if(i==selectedItems.size()-1)
                                isLast = true;
                            new sendTasks(isFirst, isLast).execute(avaliableSprints.get(selectedItems.get(i)).getTable_name());
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create();
        dialog.show();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item))
            return true;
        if(item.getGroupId() == R.id.actionbarGroup){
            return showSaveAlertDialog();
        }
        if(item.getGroupId() == R.id.actionbarGroupRefresh){
            tasksBoardFragment.refresh();
        }
        if(item.getGroupId() == R.id.actionBarGroupManageTask){
            taskFragment.showManageOptions();
        }
        if(item.getGroupId() == R.id.actionbarGroupRefreshYourTasks){
            myTasksFragment.refresh();
        }
        if(item.getGroupId() == R.id.actionBarGroupRefreshSummaries){
            sprintSummaryFragment.refresh();
        }
        if(item.getGroupId() == R.id.actionBarGroupDeleteSummary){
            sprintSummaryDataFragment.showDeleteSummary();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        showMenuSpecialy(SavedInstanceStateB);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private class sendTasks extends AsyncTask<String, Void, String>
    {

        Boolean isFirst;
        Boolean isLast;

        public sendTasks(Boolean isFirst, Boolean isLast){
            this.isFirst = isFirst;
            this.isLast = isLast;
        }


        @Override
        protected String doInBackground(String... strings) {
            String tableName = strings[0];
            try {
                URL url = new URL("http://remes.ct8.pl/newtask.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String request;

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                request = URLEncoder.encode("howmany", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(tasks.size()), "UTF-8");
                request += "&" + URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(tableName, "UTF-8");
                request += "&" + URLEncoder.encode("serverpassword", "UTF-8") + "=" + URLEncoder.encode(MainActivity.SERVERPASSWORD, "UTF-8");

                for(int i=1; i<=tasks.size(); i++)
                {
                    request += "&" + URLEncoder.encode("title" + Integer.toString(i), "UTF-8") + "=" + URLEncoder.encode(tasks.get(i-1).getTitle(), "UTF-8");
                    request += "&" + URLEncoder.encode("description" + Integer.toString(i), "UTF-8") + "=" + URLEncoder.encode(tasks.get(i-1).getDescription(), "UTF-8");
                }

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
            if(isFirst);
                AddTasksFragment.ADD_TASKS_FRAGMENT.showProgressBar();
        }

        @Override
        protected void onPostExecute(String s) {

            if(s.equals("TRUE"))
                Toast.makeText(MainActivity.this, getResources().getString(R.string.tasksSuccesfulAdded), Toast.LENGTH_SHORT).show();
            else if(s.equals("FAILED"))
                Toast.makeText(MainActivity.this, getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
            else if(s.equals("Error"))
                Toast.makeText(MainActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

            if(isLast) {
                AddTasksFragment.ADD_TASKS_FRAGMENT.hideProgressBar();
                tasks.clear();
            }
        }
    }

}
