package pl.nieruchalski.scrumfamily;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import pl.nieruchalski.scrumfamily.Database.SprintsDatabaseHelper;
import pl.nieruchalski.scrumfamily.Fragments.LogInFragment;

public class SplashScreen extends Activity {

    private static long howlong = 0;
    private static long startTime;
    private static long endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SQLiteOpenHelper helper = new SprintsDatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("REMEMBERME", null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            new autoLogIn().execute(new String[]{cursor.getString(1), cursor.getString(2)});
        }
        else {
            waitTime();
            MainActivity.NICKNAME = "@EMPTY@";
        }
        db.close();

    }

    public void waitTime()
    {

        howlong = 1000 - howlong;

        if(howlong < 0)
            howlong = 0;

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SplashScreen.this.startActivity(intent);
                SplashScreen.this.overridePendingTransition(0, 0);
            }
        }, howlong);
    }


    private class autoLogIn extends AsyncTask<String[], Void, String[]> {
        @Override
        protected void onPreExecute() {
            startTime = System.currentTimeMillis();
        }

        @Override
        protected String[] doInBackground(String[]... strings) {

            String local[] = strings[0];

            try {
                URL url = new URL("http://remes.ct8.pl/logowanie.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

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

                conn.disconnect();

                if(readed.equals("FALSE"))
                    return new String[]{"FALSE"};
                else
                    return new String[]{readed, local[0], local[1]};

            } catch (Exception e) {
                e.printStackTrace();
                return new String[]{"FAILED", local[0], local[1]};
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

            endTime = System.currentTimeMillis();
            howlong = endTime - startTime;

            if(strings[0].equals("FALSE"))
            {
                MainActivity.NICKNAME = "@FAILED@";
            }

            else if(strings[0].equals("FAILED"))//błąd połączenia
            {
                LogInFragment.ET_LOG_STATE = strings[1];
                LogInFragment.ET_PAS_STATE = strings[2];
                LogInFragment.REMEMBER_ME_STATE = true;
                MainActivity.NICKNAME = "@FAILED@";

                LogInFragment.ET_PAS_ENABLED = false;
                LogInFragment.ET_LOG_ENABLED = false;
                LogInFragment.REMEMBER_ME_ENABLED = false;

                LogInFragment.BUTTON_LOG_IN_TEXT = getResources().getString(R.string.logOut);
                LogInFragment.RECONNECT_BUTTON_VISIBLE = true;

                MainActivity.LOGGED_IN = false;
                MainActivity.OFFLINE_MODE = true;

            }

            else
            {
                MainActivity.LOGGED_IN = true;
                LogInFragment.ET_PAS_STATE = strings[2];
                LogInFragment.ET_LOG_STATE = strings[1];
                LogInFragment.BUTTON_LOG_IN_TEXT = getResources().getString(R.string.logOut);
                LogInFragment.REMEMBER_ME_STATE = true;

                LogInFragment.ET_PAS_ENABLED = false;
                LogInFragment.ET_LOG_ENABLED = false;
                LogInFragment.REMEMBER_ME_ENABLED = false;

                MainActivity.NICKNAME = strings[0];
            }

            waitTime();

        }


    }
}
