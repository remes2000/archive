package pl.nieruchalski.scrumfamily.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pl.nieruchalski.scrumfamily.R;

/**
 * Created by michal on 19.01.17.
 */

public class CustomAdapter extends ArrayAdapter<String> {

    int[] images = {};
    int[] textColors = {};
    int[] backgroundColors = {};

    ArrayList<String> options;
    Context c;
    LayoutInflater inflater;

    public CustomAdapter(Context con, int[] img, ArrayList<String> opt, int[] txtColors, int[] bcgColors)
    {
        super(con, R.layout.model, opt);
        this.c = con;
        this.options = opt;
        this.images = img;
        this.textColors = txtColors;
        this.backgroundColors = bcgColors;
    }

    public class CustomViewHolder
    {
        TextView option;
        ImageView icon;
        LinearLayout linearLayout;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.model, null);
        }

        final CustomViewHolder holder = new CustomViewHolder();

        holder.option = (TextView) convertView.findViewById(R.id.modelTv);
        holder.icon = (ImageView) convertView.findViewById(R.id.modelIv);
        holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.modelLinearLayout);

        holder.option.setText(options.get(position));
        holder.icon.setImageResource(images[position]);
        holder.option.setTextColor(textColors[position]);
        holder.linearLayout.setBackgroundColor(backgroundColors[position]);

        return convertView;
    }
}
