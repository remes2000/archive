package pl.nieruchalski.scrumfamily.Fragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

import pl.nieruchalski.scrumfamily.Database.SprintsDatabaseHelper;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {

    public RegistrationFragment() {
        // Required empty public constructor
    }

    private EditText emailAdress, password, passwordRepeat, nickname;
    private CheckBox showPasswords;
    private Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        getActivity().setTitle(getResources().getString(R.string.registerNowv2));

        emailAdress = (EditText) view.findViewById(R.id.etEmailRegister);
        password = (EditText) view.findViewById(R.id.etPasswordRegister);
        passwordRepeat = (EditText) view.findViewById(R.id.eTRepeatPasswordRegister);
        nickname = (EditText) view.findViewById(R.id.etNickRegister);
        showPasswords = (CheckBox) view.findViewById(R.id.showPasswordsRegister);
        button = (Button) view.findViewById(R.id.registerNowButton);


        showPasswords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showPasswords.isChecked())
                {
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    passwordRepeat.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else
                {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordRepeat.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean eA = isEmailValid(emailAdress.getText().toString().trim());
                boolean pass = passCorrect(password.getText().toString(), passwordRepeat.getText().toString());
                boolean all = allCompleted(emailAdress.getText().toString(), nickname.getText().toString(), password.getText().toString(), passwordRepeat.getText().toString());
                boolean nick = isNickOk(nickname.getText().toString());
                if(eA && pass && all && nick)
                    new registerNow().execute(new String[]{emailAdress.getText().toString().trim(), password.getText().toString(), nickname.getText().toString()});
                else if(!nick)
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.nickError), Toast.LENGTH_SHORT).show();
                else if(!eA)
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getString(R.string.wrongEmailAdress), Toast.LENGTH_SHORT).show();
                else if(!pass)
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.passwordsAreNotTheSame), Toast.LENGTH_SHORT).show();
                else if(!all)
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.completeAllBlanks), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).accFragment = "RegistrationFragment";
        ((MainActivity) getActivity()).underlineOption();

    }

    private boolean isNickOk(String nick)
    {
        int l = nick.length();
        for(int i=0; i<l; i++)
        {
            int ch = (int) nick.charAt(i);

            if((ch>=65)&&(ch<=90)){;}
            else if((ch>=97)&&(ch<=122)){;}
            else if((ch>=48)&&(ch<=57)){;}
            else
                return false;
        }
        return true;
    }

    private boolean allCompleted(String a, String b, String c, String d)
    {
        return !(a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty());
    }

    private boolean passCorrect(String p1, String p2){
        if(p1.equals(p2))
        {
            if(!p1.isEmpty())
                return true;
            else return false;
        }
        else
            return false;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private class registerNow extends AsyncTask<String[], Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            (   (ProgressBar)   getView().findViewById(R.id.progressBarRegister)).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String[]... strings) {
            try {
                URL url = new URL("http://remes.ct8.pl/rejestracja.php");
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
                request += "&" + URLEncoder.encode("nick", "UTF-8") + "=" + URLEncoder.encode(local[2], "UTF-8");
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

            (   (ProgressBar)   getView().findViewById(R.id.progressBarRegister)).setVisibility(View.INVISIBLE);

            if (b.equals("FAILED")) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connectionFailed), Toast.LENGTH_SHORT).show();
            }
            else if (b.equals("TRUE")) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.correctlyRegistered), Toast.LENGTH_SHORT).show();
                emailAdress.setText("");
                password.setText("");
                passwordRepeat.setText("");
                nickname.setText("");
            }
            else if (b.equals("FALSE_NICK")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.registerFailedByNick), Toast.LENGTH_SHORT).show();
            }
            else if(b.equals("FALSE_EMAIL")){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.registerFailedByEmail), Toast.LENGTH_SHORT).show();
            }


        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}
