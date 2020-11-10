package com.kevinah.sudoku;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InformationFragment extends Fragment {
    private static final String TAG = "InformationFragment";

    public InformationFragment() {
        // Required empty public constructor
    }

    public static InformationFragment newInstance() {
        InformationFragment fragment = new InformationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);
        initBackButton(rootView);
        return rootView;
    }

    private void initBackButton(View rootView){
        Button backButton = rootView.findViewById(R.id.button_back_information);
        backButton.setOnClickListener(v->{
            Log.i(TAG,"Pressed the back button");
            getParentFragmentManager().beginTransaction().remove(InformationFragment.this).commit();
            getParentFragmentManager().popBackStack();
        });
    }
}