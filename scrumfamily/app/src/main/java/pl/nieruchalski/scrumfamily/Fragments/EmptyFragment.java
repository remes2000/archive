package pl.nieruchalski.scrumfamily.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pl.nieruchalski.scrumfamily.MainActivity;
import pl.nieruchalski.scrumfamily.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyFragment extends Fragment {

    private Button button;
    private TextView textView;

    private Boolean isButtonVisable = false;
    private String buttonText = "";
    private String errorText = "";
    private static Fragment fragment;
    private ImageView box;

    public EmptyFragment() {
        // Required empty public constructor
    }

    public void setFragmentOptions(Boolean isButtonVisable, String buttonText, String errorText, Fragment frag)
    {
        this.isButtonVisable = isButtonVisable;
        this.buttonText = buttonText;
        this.errorText = errorText;
        fragment = frag;
    }

    private void prepare()
    {
        button.setText(buttonText);

        if(isButtonVisable){
            button.setVisibility(View.VISIBLE);}
        else
            button.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fragment instanceof AddTasksFragment)
                    ((MainActivity) getActivity()).updateFragment(AddTasksFragment.ADD_TASKS_FRAGMENT, false);
                else
                    ((MainActivity) getActivity()).updateFragment(fragment, false);
            }
        });

        textView.setText(errorText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_empty, container, false);

        if(savedInstanceState != null){
            isButtonVisable = savedInstanceState.getBoolean("isButtonVisable");
            buttonText = savedInstanceState.getString("buttonText");
            errorText = savedInstanceState.getString("errorText");
        }

        button = (Button) view.findViewById(R.id.changeItButton);
        textView = (TextView) view.findViewById(R.id.errorTitle);
        box = (ImageView) view.findViewById(R.id.emptyBox);

        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "Icon made by freepik.com from flaticon.com is licensed by creativecommons.org/licenses/by/3.0", Toast.LENGTH_SHORT).show();
            }
        });

        prepare();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isButtonVisable", isButtonVisable);
        outState.putString("buttonText", buttonText);
        outState.putString("errorText", errorText);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.app_name));
        MainActivity.isEmptyErrorOnForegroundFlag = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.isEmptyErrorOnForegroundFlag = false;}
    }
