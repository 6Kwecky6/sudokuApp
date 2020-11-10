package com.kevinah.sudoku;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaveBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaveBoardFragment extends Fragment {
    private static final String TAG = "SaveBoardFragment";
    private ArrayList<String> board;
    private int checkedDifficultyID=-1;

    public SaveBoardFragment() {
        // Required empty public constructor
    }

    public static SaveBoardFragment newInstance(ArrayList<String> board) {
        SaveBoardFragment fragment = new SaveBoardFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("board",board);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            Log.i(TAG,"Loading board to be saved..");
            board= getArguments().getStringArrayList("board");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_save_board, container, false);
        initDifficultyRadioGroup(rootView);
        initSaveButton(rootView);
        initCancelButton(rootView);
        return rootView;
    }

    private void initDifficultyRadioGroup(View rootView){
        RadioGroup difficultyRadioGroup = rootView.findViewById(R.id.difficulty_radio_group);
        difficultyRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Log.i(TAG,"Radio button clicked. ID: "+checkedId);
            checkedDifficultyID=checkedId;
        });
    }
    private void initSaveButton(View rootView){
        Button saveButton = rootView.findViewById(R.id.button_save_fragment);
        saveButton.setOnClickListener(v->{
            if(checkedDifficultyID!=-1){
                Log.i(TAG,"Saving board to external drive");
                SaveBoard();
                Intent intent = new Intent(requireActivity(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            }else{
                Log.i(TAG,"Tried saving without selecting difficulty");
                Toast toast = Toast.makeText(requireActivity().getApplicationContext(),getResources().getText(R.string.toast_no_selected_difficulty),Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void SaveBoard(){
        //TODO: allow user to toggle between external directory and local
        File dir = requireActivity().getFilesDir();
        String difficulty;
        if(checkedDifficultyID==R.id.radio_easy){
            Log.i(TAG,"Saving board as easy difficulty");
            difficulty="easy";
        }else if(checkedDifficultyID==R.id.radio_medium){
            Log.i(TAG,"Saving board as medium difficulty");
            difficulty="medium";
        }else if(checkedDifficultyID==R.id.radio_hard){
            Log.i(TAG,"Saving board as hard difficulty");
            difficulty="hard";
        }else{
            Log.e(TAG, "Illegal radio button");
            difficulty=null;
        }
        File file_board=new File(dir,difficulty+dir.list().length);
        Log.d(TAG,"writing board to path "+file_board.getAbsolutePath());

        try (PrintWriter writer = new PrintWriter(file_board)) {
            //Translate arraylist to the csv format I decided on
            StringBuilder board_csv = new StringBuilder();
            for (int i = 0; i < board.size(); i++) {
                if (board.get(i).equals("")) {
                    board_csv.append("_");
                } else {
                    board_csv.append(board.get(i));
                }
                if ((i + 1) % 9 == 0) {
                    board_csv.append("\n");
                } else {
                    board_csv.append(",");
                }

            }
            Log.i(TAG, "Writing this string to file: \n" + board_csv.toString());
            writer.write(board_csv.toString());

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed to write board. " + e.toString());
            Toast toast = Toast.makeText(requireActivity().getApplicationContext(), getResources().getText(R.string.toast_failed_to_save_board), Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void initCancelButton(View rootView){
        Button cancelButton=rootView.findViewById(R.id.button_cancel_save_fragment);
        cancelButton.setOnClickListener(v->{
            Log.i(TAG,"Cancelling save board");
            getParentFragmentManager().beginTransaction().remove(SaveBoardFragment.this).commit();
        });
    }
}