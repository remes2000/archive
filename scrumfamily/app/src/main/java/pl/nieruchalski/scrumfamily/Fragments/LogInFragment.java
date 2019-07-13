package pl.nieruchalski.scrumfamily.Fragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.StrictMode;
import android.util.Log;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import pl.nieruchalski.scrumfamily.Database.SprintsDatabaseHelper;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogInFragment extends Fragment {

    private MainActivity mA;
    private Button registerNow, logInButton, reconnectButton;
    private EditText etLogin, etPassword;
    private CheckBox rememberMe;

    public static boolean ET_LOG_ENABLED = true;
    public static boolean ET_PAS_ENABLED = true;
    public static boolean REMEMBER_ME_ENABLED = true;

    public static String ET_LOG_STATE = "";
    public static String ET_PAS_STATE = "";
    public static String BUTTON_LOG_IN_TEXT = "";
    public static boolean REMEMBER_ME_STATE = false;

    public static boolean RECONNECT_BUTTON_VISIBLE = false;

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        if(BUTTON_LOG_IN_TEXT.equals(""))
            BUTTON_LOG_IN_TEXT = getResources().getString(R.string.logIn);

        getActivity().setTitle(getResources().getString(R.string.logInToApp));

        registerNow = (Button) view.findViewById(R.id.buttonRegisterNow);
        logInButton = (Button) view.findViewById(R.id.buttonLogin);
        reconnectButton = (Button) view.findViewById(R.id.buttonReconnect);

        etLogin = (EditText) view.findViewById(R.id.eTlogin);
        etPassword = (EditText) view.findViewById(R.id.eTpassword);

        rememberMe = (CheckBox) view.findViewById(R.id.logInRememberMe);

        prepare();

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(logInButton.getText().equals(getResources().getString(R.string.logIn))) {
                    if((!etLogin.getText().toString().isEmpty())&&(!etPassword.getText().toString().isEmpty())) {
                        etLogin.setText(etLogin.getText().toString().trim());
                        new goIntoInternet().execute(new String[]{etLogin.getText().toString(), etPassword.getText().toString()});
                    }
                    else
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.completeAllBlanks), Toast.LENGTH_SHORT).show();
                }
                else {
                    MainActivity.LOGGED_IN = false;
                    logInButton.setText(getResources().getString(R.string.logIn));
                    etPassword.setEnabled(true);
                    etLogin.setEnabled(true);
                    rememberMe.setEnabled(true);
                    etPassword.setText("");
                    etLogin.setText("");
                    rememberMe.setChecked(false);
                    SQLiteOpenHelper helper = new SprintsDatabaseHelper(getActivity().getApplicationContext());
                    SQLiteDatabase db = helper.getWritableDatabase();
                    db.execSQL("delete from "+ "REMEMBERME");
                    db.execSQL("delete from " + "MYTASKS");
                    db.close();
                    reconnectButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        reconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new goIntoInternet().execute(new String[]{etLogin.getText().toString(), etPassword.getText().toString()});
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).accFragment = "LogInFragment";
        ((MainActivity) getActivity()).underlineOption();

        mA = (MainActivity) getActivity();

        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new RegistrationFragment();
                mA.updateFragment(fragment, true);
            }
        });
    }

    private void prepare()
    {
        etPassword.setText(LogInFragment.ET_PAS_STATE);
        etLogin.setText(LogInFragment.ET_LOG_STATE);
        logInButton.setText(LogInFragment.BUTTON_LOG_IN_TEXT);
        rememberMe.setChecked(LogInFragment.REMEMBER_ME_STATE);

        etLogin.setEnabled(LogInFragment.ET_LOG_ENABLED);
        etPassword.setEnabled(LogInFragment.ET_PAS_ENABLED);
        rememberMe.setEnabled(LogInFragment.REMEMBER_ME_ENABLED);

        if(RECONNECT_BUTTON_VISIBLE)
            reconnectButton.setVisibility(View.VISIBLE);
        else
            reconnectButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogInFragment.ET_PAS_STATE = etPassword.getText().toString();
        LogInFragment.ET_LOG_STATE = etLogin.getText().toString();
        LogInFragment.BUTTON_LOG_IN_TEXT = logInButton.getText().toString();
        LogInFragment.REMEMBER_ME_STATE = rememberMe.isChecked();

        LogInFragment.ET_PAS_ENABLED = etPassword.isEnabled();
        LogInFragment.ET_LOG_ENABLED = etLogin.isEnabled();
        LogInFragment.REMEMBER_ME_ENABLED = rememberMe.isEnabled();

        if(reconnectButton.getVisibility() == View.VISIBLE)
            RECONNECT_BUTTON_VISIBLE = true;
        else
            RECONNECT_BUTTON_VISIBLE = false;

    }

    private class goIntoInternet extends AsyncTask<String[], Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((Button) getView().findViewById(R.id.buttonRegisterNow)).setEnabled(false);
            ((Button) getView().findViewById(R.id.buttonLogin)).setEnabled(false);
            ((Button) getView().findViewById(R.id.buttonReconnect)).setEnabled(false);
            ((EditText) getView().findViewById(R.id.eTlogin)).setEnabled(false);
            ((EditText) getView().findViewById(R.id.eTpassword)).setEnabled(false);
            ((CheckBox) getView().findViewById(R.id.logInRememberMe)).setEnabled(false);

            ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String[]... strings) {
            try {
                URL url = new URL("http://remes.ct8.pl/logowanie.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String local[] = strings[0];

                String request;

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                request = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(local[0], "UTF-8");
                request += "&" + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(local[1], "UTF-8");
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
        protected void onPostExecute(String b) {

            ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);

            if (b.equals("FAILED")) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
                ((Button) getView().findViewById(R.id.buttonRegisterNow)).setEnabled(true);
                ((Button) getView().findViewById(R.id.buttonReconnect)).setEnabled(true);
                ((Button) getView().findViewById(R.id.buttonLogin)).setEnabled(true);
                ((EditText) getView().findViewById(R.id.eTlogin)).setEnabled(true);
                ((EditText) getView().findViewById(R.id.eTpassword)).setEnabled(true);
                ((CheckBox) getView().findViewById(R.id.logInRememberMe)).setEnabled(true);

                if(RECONNECT_BUTTON_VISIBLE){
                    ((EditText) getView().findViewById(R.id.eTlogin)).setEnabled(false);
                    ((EditText) getView().findViewById(R.id.eTpassword)).setEnabled(false);
                    ((CheckBox) getView().findViewById(R.id.logInRememberMe)).setEnabled(false);
                }

            }
            else if (b.equals("FALSE")) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.logInFailed), Toast.LENGTH_SHORT).show();
                ((Button) getView().findViewById(R.id.buttonRegisterNow)).setEnabled(true);
                ((Button) getView().findViewById(R.id.buttonLogin)).setEnabled(true);
                ((Button) getView().findViewById(R.id.buttonReconnect)).setEnabled(true);
                ((EditText) getView().findViewById(R.id.eTlogin)).setEnabled(true);
                ((EditText) getView().findViewById(R.id.eTpassword)).setEnabled(true);
                ((CheckBox) getView().findViewById(R.id.logInRememberMe)).setEnabled(true);
            }
            else{
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.welcome) + " " + b + " !", Toast.LENGTH_SHORT).show();
                MainActivity.LOGGED_IN = true;
                MainActivity.OFFLINE_MODE = false;
                MainActivity.NICKNAME = b;

                SQLiteOpenHelper helper = new SprintsDatabaseHelper(getActivity().getApplicationContext());
                SQLiteDatabase db = helper.getWritableDatabase();

                if (((CheckBox) getView().findViewById(R.id.logInRememberMe)).isChecked()) {

                    String count = "SELECT count(*) FROM REMEMBERME";
                    Cursor cursor = db.rawQuery(count, null);
                    cursor.moveToFirst();
                    int icount = cursor.getInt(0);

                    if (icount > 0)
                        db.execSQL("delete from " + "REMEMBERME");

                    ContentValues CV = new ContentValues();
                    CV.put("LOGIN", ((EditText) getView().findViewById(R.id.eTlogin)).getText().toString());
                    CV.put("PASSWORD", ((EditText) getView().findViewById(R.id.eTpassword)).getText().toString());
                    db.insert("REMEMBERME", null, CV);
                    db.close();
                }
                else {
                    db.execSQL("delete from " + "REMEMBERME");
                    db.close();
                }

                ((Button) getView().findViewById(R.id.buttonLogin)).setText(getResources().getString(R.string.logOut));
                ((Button) getView().findViewById(R.id.buttonRegisterNow)).setEnabled(true);
                ((Button) getView().findViewById(R.id.buttonReconnect)).setEnabled(true);
                ((Button) getView().findViewById(R.id.buttonLogin)).setEnabled(true);
                ((EditText) getView().findViewById(R.id.eTlogin)).setEnabled(false);
                ((EditText) getView().findViewById(R.id.eTpassword)).setEnabled(false);
                ((CheckBox) getView().findViewById(R.id.logInRememberMe)).setEnabled(false);

                ((Button) getView().findViewById(R.id.buttonReconnect)).setVisibility(View.INVISIBLE);

            }
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}