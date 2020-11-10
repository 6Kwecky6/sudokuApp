package com.kevinah.sudoku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 * This fragment will allow the user to choose which board to solve
 */
public class ChooseBoardFragment extends Fragment {
    private Context context;

    private final static String TAG = "ChooseBoardFragment";
    public ChooseBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment ChooseBoardFragment.
     */
    public static ChooseBoardFragment newInstance() {
        return new ChooseBoardFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //Saving context for later to prevent nullptr
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(TAG,"Arguments: "+getArguments().toString());
        }else{
            Log.e(TAG,"arguments is null, this will likely break the app");
        }
    }

    private void initCancelButton(View rootView){
        Button cancelButton = rootView.findViewById(R.id.button_cancel_board_selection);
        cancelButton.setOnClickListener(v -> {
            //Kills this fragment
            Log.i(TAG,"cancel button has been pressed");
            ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().remove(this).commit();
            ((AppCompatActivity)context).getSupportFragmentManager().popBackStack();
        });
    }
    private void initEasyButton(View rootView){
        Button easyButton = rootView.findViewById(R.id.easy_board_button);
        easyButton.setOnClickListener(v -> {
            Log.i(TAG,"Easy difficulty selected");
            InputStream ins = selectRandomFile(rootView,"easy");
            ArrayList<String> board = getSudokuBoard(ins);
            Log.i(TAG,"Got this board: "+board.toString());
            Intent intent = new Intent(requireActivity(),SudokuActivity.class);
            intent.putStringArrayListExtra(SudokuActivity.GET_BOARD,board);
            intent.putExtra(SudokuActivity.CREATOR_MODE,false);
            startActivity(intent);
        });

    }
    private void initMediumButton(View rootView){
        Button mediumButton=rootView.findViewById(R.id.medium_board_button);
        mediumButton.setOnClickListener(v -> {
            Log.i(TAG,"Medium difficulty selected");
            InputStream ins = selectRandomFile(rootView,"medium");
            ArrayList<String> board = getSudokuBoard(ins);
            Log.i(TAG, "Got this board: "+ board.toString());
            Intent intent = new Intent(requireActivity(),SudokuActivity.class);
            intent.putStringArrayListExtra(SudokuActivity.GET_BOARD,board);
            startActivity(intent);
        });
    }
    public void initHardButton(View rootView){
        Button hardButton=rootView.findViewById(R.id.hard_board_button);
        hardButton.setOnClickListener(v -> {
            Log.i(TAG,"Hard difficulty selected");
            InputStream ins = selectRandomFile(rootView,"hard");
            ArrayList<String> board = getSudokuBoard(ins);
            Log.i(TAG, "Got this board: "+ board.toString());
            Intent intent = new Intent(requireActivity(),SudokuActivity.class);
            intent.putStringArrayListExtra(SudokuActivity.GET_BOARD,board);
            startActivity(intent);
        });
    }
    //This should return one InputStream at random based on difficulty
    private InputStream selectRandomFile(View rootView, String difficulty){
        ArrayList<Object> allBoards =getRawFiles(difficulty);//will be either an Integer for res/raw/[file], or File for everything else
        allBoards.addAll(getLocalFiles(difficulty));

        Object chosen = allBoards.get(new Random().nextInt(allBoards.size()));
        if(chosen instanceof Integer){

            Log.i(TAG,"Chosen file is an id, treating is as a res/raw file");
            return rootView.getResources().openRawResource((int)chosen);

        }if(chosen instanceof File){

            Log.i(TAG,"Chosen file is a File type with path"+((File) chosen).getAbsolutePath());
            try {
                return new FileInputStream((File) chosen);
            }catch(FileNotFoundException e){
                Log.e(TAG,"The chosen file could not be found. "+e.toString());
                return null;
            }

        }else{
            Log.e(TAG,"Unexpected typing of chosen file");
            return null;
        }
    }



    private ArrayList<Object> getExternalFiles(){
        //TODO:add support for external files. This can wait til I can add sudoku boards externally
        return null;
    }

    private ArrayList<Object> getServerFiles(){
        //TODO:add support for loading files from the sftp server
        return null;
    }

    private ArrayList<Object> getLocalFiles(String difficulty){
        Log.i(TAG,"Collecting all files in local storage and filtering the correct difficulty");
        ArrayList<Object> rawFiles=new ArrayList<>();
        File[] files =requireActivity().getFilesDir().listFiles();
        if(files!=null) {
            for (File file : files) {
                if (file.getName().contains(difficulty)) {
                    Log.i(TAG, "Bringing local file " + file.getName() + " for randomly being chosen");
                    rawFiles.add(file);
                }
            }
        }else{
            Log.e(TAG,"Could not load files");
        }
        return rawFiles;
    }

    private ArrayList<Object> getRawFiles(String difficulty){
        Log.i(TAG,"Collecting all files in res/raw and filtering the correct difficulty");
        Field[] fields= R.raw.class.getDeclaredFields();
        ArrayList<Object> rawFiles=new ArrayList<>();
        for (Field field : fields) {
            if (field.getName().contains(difficulty)) {
                Log.i(TAG, "Bringing raw file at res/raw/" + field.getName() + " for randomly being chosen");
                try {
                    rawFiles.add(field.getInt(field));
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "IllegalAccessException for getting ID for the file res/raw/" + field.getName());
                }

            }else{
                Log.d(TAG,"Filtering away res/raw/"+field.getName());
            }
        }
        return rawFiles;
    }

    private ArrayList<String> getSudokuBoard(InputStream ins){
        Log.i(TAG,"Loading board...");
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        String[] line;
        ArrayList<String> board=new ArrayList<>();
        try {
            while (reader.ready()){
                line=reader.readLine().split(",");
                for (String s : line) {
                    if (s.equals("_")) {
                        board.add("");//0 should signify empty cell
                    } else {
                        board.add(s);
                    }
                }
            }
        }catch(IOException e){
            Log.e(TAG,"Failed to read ");
        }finally {
            try{reader.close();}catch (IOException ignored){}
            try{ins.close();}catch (IOException ignored){}
        }
        return board;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_choose_board, container, false);
        initCancelButton(rootView);
        initHardButton(rootView);
        initMediumButton(rootView);
        initEasyButton(rootView);
        return rootView;
    }
}