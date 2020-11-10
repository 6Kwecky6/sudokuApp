package com.kevinah.sudoku;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SudokuViewAdapter extends BaseAdapter {
    private static final String TAG = "SudokuViewAdapter";
    private final ArrayList<String> hints;
    private final ArrayList<String> cells;
    private final Activity mActivity;
    private static LayoutInflater inflater = null;
    private int selectedPosition =-1;
    private int mode =-1;

    public SudokuViewAdapter(Activity activity,ArrayList<String> hints){
        Log.i(TAG,"Created adapter with hints");
        mActivity=activity;
        this.hints=hints;
        this.cells=(ArrayList<String>)hints.clone();
        inflater=(LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public SudokuViewAdapter(Activity activity){
        Log.i(TAG,"Created adapter without hints");
        mActivity=activity;
        this.hints=new ArrayList<>(Arrays.asList(new String[81]));
        this.cells=new ArrayList<>(Arrays.asList(new String[81]));

        Collections.fill(this.hints,"");
        Collections.fill(this.cells,"");

        inflater=(LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public final int getCount() {
        Log.d(TAG,"getCount gives array size: "+hints.size());
        return hints.size();
    }

    @Override
    public final Object getItem(int position) {
        Log.d(TAG,"getItem gives: "+hints.get(position));
        return hints.get(position);
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }

    public ArrayList<String> getHints(){
        return hints;
    }

//    private void toggleItemActive(int position){
//        active.set(position, false);
//        notifyDataSetChanged();
//    }

    public void setHint(int postiton,String val){
        hints.set(postiton,val);
        cells.set(postiton,val);
    }

    public void setSelectedPosition(int selectedPosition,int mode) {
        this.selectedPosition = selectedPosition;
        this.mode=mode;
    }

    public void setCell(int position, String val){
        cells.set(position,val);
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }

    public int getSelectedMode(){
        return mode;
    }

    public boolean isItemActive(int position){
        return hints.get(position).equals("");
    }

    public boolean isNoEmpty(){
        return !cells.contains("");
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d(TAG,"Create view for position "+position);
        if(parent!=null)Log.d(TAG,"parent ViewGroup"+parent.toString());
        else Log.d(TAG,"Parent viewGroup is null");
        if(convertView!=null)Log.d(TAG,"ConvertView: "+convertView.toString());
        else Log.d(TAG,"ConvertView is null");
        //TODO: Translate to recycleView
        View view =inflater.inflate(R.layout.cell_view, parent,false);
        TextView answerTextView=view.findViewById(R.id.answer);
        answerTextView.setText(hints.get(position));

        ConstraintLayout possibilitiesView = view.findViewById(R.id.possibilities_grid);


        //Make sure that the help grid only stays visible if there isn't a selected answer
        if(!hints.get(position).equals("")){
            possibilitiesView.setVisibility(View.INVISIBLE);
        }else{
            possibilitiesView.setVisibility(View.VISIBLE);
        }

        return view;
    }

}
