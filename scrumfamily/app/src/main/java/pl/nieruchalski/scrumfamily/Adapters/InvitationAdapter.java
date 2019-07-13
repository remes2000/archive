package pl.nieruchalski.scrumfamily.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

import pl.nieruchalski.scrumfamily.Fragments.LogInFragment;
import pl.nieruchalski.scrumfamily.Fragments.YourInvitationFragment;
import pl.nieruchalski.scrumfamily.HelpingClasses.Invitation;
import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * Created by michal on 08.02.17.
 */

public class InvitationAdapter extends ArrayAdapter<Invitation> {
    ArrayList<Invitation> invitations;
    Context c;
    LayoutInflater inflater;
    YourInvitationFragment fragment;

    public InvitationAdapter(Context c, ArrayList<Invitation> invitations, YourInvitationFragment fragment)
    {
        super(c, R.layout.invitation_model, invitations);
        this.invitations = invitations;
        this.c = c;
        this.fragment = fragment;
    }

    public class CustomViewHolder{
        TextView sprintTitle;
        TextView from;
        ImageView accept;
        ImageView decline;
    }

    public String titleToUseless(String title)
    {
        StringBuilder builder = new StringBuilder(title);
        for(int i=0; i<title.length(); i++)
        {
            if(title.charAt(i) == '_')
                builder.setCharAt(i, ' ');
        }
        return builder.toString();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.invitation_model, null);
        }

        final InvitationAdapter.CustomViewHolder holder = new InvitationAdapter.CustomViewHolder();

        holder.sprintTitle = (TextView) convertView.findViewById(R.id.sprintTitleInvitation);
        holder.from = (TextView) convertView.findViewById(R.id.fromInvitation);
        holder.accept = (ImageView) convertView.findViewById(R.id.buttonAcceptInvitation);
        holder.decline = (ImageView) convertView.findViewById(R.id.buttonDeclineInvitation);

        holder.sprintTitle.setTextColor(Color.BLACK);
        holder.from.setTextColor(Color.BLACK);

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new acceptDeclineInvitation(invitations.get(position)).execute("TRUE");
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new acceptDeclineInvitation(invitations.get(position)).execute("FALSE");
            }
        });

        holder.sprintTitle.setText(titleToUseless(invitations.get(position).getSprint().getTitle()));
        holder.from.setText(invitations.get(position).getSprint().getUsernick());

        return convertView;
    }

    private class acceptDeclineInvitation extends AsyncTask<String, Void, String> {

        Invitation invitation;

        public acceptDeclineInvitation(Invitation invitation){
            this.invitation = invitation;
        }

        @Override
        protected String doInBackground(String... strings) {

            String local = strings[0];

            try {
                URL url = new URL("http://remes.ct8.pl/acceptdeclineinvitation.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                String request;

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                request = URLEncoder.encode("acceptdecline", "UTF-8") + "=" + URLEncoder.encode(local, "UTF-8");
                request += "&" + URLEncoder.encode("usermail", "UTF-8") + "=" + URLEncoder.encode(LogInFragment.ET_LOG_STATE, "UTF-8");
                request += "&" + URLEncoder.encode("usernick2", "UTF-8") + "=" + URLEncoder.encode(MainActivity.NICKNAME, "UTF-8");
                request += "&" + URLEncoder.encode("usernick", "UTF-8") + "=" + URLEncoder.encode(invitation.getSprint().getUsernick(), "UTF-8");
                request += "&" + URLEncoder.encode("sprinttitle", "UTF-8") + "=" + URLEncoder.encode(invitation.getSprint().getTitle(), "UTF-8");
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
            fragment.showProgressBar();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            fragment.hideProgressBar();
            fragment.getInvitations();
        }
    }

}
