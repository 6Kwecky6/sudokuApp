package com.kevinah.sudoku;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VictoryScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VictoryScreenFragment extends Fragment {
    private static final String TAG ="VictoryScreenFragment";
    private ArrayList<String> board;

    public VictoryScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VictoryScreenFragment.
     */
    public static VictoryScreenFragment newInstance(ArrayList<String> board) {
        VictoryScreenFragment fragment = new VictoryScreenFragment();
        Bundle args = new Bundle();
        //TODO: show play time
        args.putStringArrayList("board",board);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(getArguments()!=null){
            Log.d(TAG,"Victory screen received these arguments: "+getArguments().toString());
            board=getArguments().getStringArrayList("board");
        }
        super.onCreate(savedInstanceState);
    }

    private void initQuitButton(View rootView){
        Button backButton = rootView.findViewById(R.id.button_quit);
        backButton.setOnClickListener(v -> {
            Log.i(TAG,"Pressed the quit button");
            Intent intent = new Intent(requireActivity(),MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_victory_screen, container, false);
        if(requireActivity().getIntent().getBooleanExtra(SudokuActivity.CREATOR_MODE,false)){
            Log.i(TAG,"Won in creator mode. Giving option to save board");
            initSaveView(rootView);
        }else{
            ConstraintLayout saveView=rootView.findViewById(R.id.save_view);
            saveView.setVisibility(ConstraintLayout.GONE);
        }
        initQuitButton(rootView);
        return rootView;
    }

    private void initSaveView(View rootView){
        ConstraintLayout saveView = rootView.findViewById(R.id.save_view);
        saveView.setVisibility(ConstraintLayout.VISIBLE);

        Button saveButton = rootView.findViewById(R.id.button_save);
        saveButton.setOnClickListener(v->{
            Log.i(TAG,"Save button clicked");

            FragmentTransaction fragmentTransaction =getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.victory_screen,SaveBoardFragment.newInstance(board));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        Button backButton = rootView.findViewById(R.id.button_back);
        backButton.setOnClickListener(v->{
            Log.i(TAG,"Back button pressed");
            getParentFragmentManager().beginTransaction().remove(VictoryScreenFragment.this).commit();
        });
    }
}