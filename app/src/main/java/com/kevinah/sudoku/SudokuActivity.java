package com.kevinah.sudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SudokuActivity extends AppCompatActivity {
    private static final String TAG = "SudokuActivity";
    public static final String CREATOR_MODE="creator_mode";
    public static final String GET_BOARD = "board";
    private static final int INVALID=-1;
    private static final int CLEAN=0;
    private static final int ANSWER=1;
    private static final int THINK=2;
    private boolean creator_mode;

    Button button1,button2,button3,button4,button5,button6,button7,button8,button9;
    ArrayList<String> board;
    ArrayList<Integer> incorrect = new ArrayList<>();
    GridView sudokuGridView;
    MenuItem togglePlayMode;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sudoku_menu, menu);
        if(getIntent().getBooleanExtra(CREATOR_MODE,false)){
            togglePlayMode = menu.findItem(R.id.toggle_play_mode);
            togglePlayMode.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i(TAG,"Option selected: "+item.toString());
        if(item.getItemId()==R.id.sudoku_settings){
            Log.d(TAG,"Handling sudoku settings");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.settings_view,SettingsFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }else if(item.getItemId()==R.id.toggle_play_mode){
            Log.d(TAG,"Handling toggle play mode");
            creator_mode=!creator_mode;
            if(creator_mode) {
                togglePlayMode.setTitle(getResources().getString(R.string.action_toggle_creator_mode));
            }else{
                togglePlayMode.setTitle(getResources().getString(R.string.action_toggle_player_mode));
            }
        }else if(item.getItemId()==R.id.button_help){
            Log.i(TAG,"The help button has been pressed");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            int start_page=1;
            if(getIntent().getBooleanExtra(CREATOR_MODE,false))start_page=4;
            fragmentTransaction.add(R.id.settings_view,HelpFragment.newInstance(start_page));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }else{
            Log.e(TAG,"Invalid item selected");

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        Log.i(TAG,"SudokuActivity created");
        initAllButtons();
        initGridView();
    }


    private void initGridView(){
        Intent intent=getIntent();
        //Creator mode:
        if(intent.getBooleanExtra(CREATOR_MODE,false)) {
            creator_mode=true;
            Log.d(TAG, "Loading in creator mode");
            SudokuViewAdapter adapter = new SudokuViewAdapter(this);
            sudokuGridView=findViewById(R.id.sudoku_board);
            sudokuGridView.setAdapter(adapter);
            //TODO: Load grid in thread, and create a loading screen fragment
            initOnItemClickListener();
            initOnItemLongClickListener();

        }//Player mode:
        else{
            Log.d(TAG,"Loading board in play mode");
            creator_mode=false;
            board = intent.getStringArrayListExtra(GET_BOARD);
            if (board != null) {
                Log.d(TAG, "Loaded the board with: " + board.size() + " elements full board:" + board.toString());
                SudokuViewAdapter adapter = new SudokuViewAdapter(this, board);
                sudokuGridView = findViewById(R.id.sudoku_board);
                sudokuGridView.setAdapter(adapter);
                //TODO: Load grid in thread, and create a loading screen fragment
                initOnItemClickListener();
                initOnItemLongClickListener();
            } else Log.e(TAG, "Could not load the board");
        }
    }

    private void initOnItemClickListener(){
        sudokuGridView.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(TAG,"Clicked on item with position "+position);
            if(((SudokuViewAdapter)sudokuGridView.getAdapter()).isItemActive(position)||creator_mode) {
                Log.i(TAG, "Item is active");
                Log.d(TAG,"previous selected item position: "+ ((SudokuViewAdapter) sudokuGridView.getAdapter()).getSelectedPosition());

                //Removing colors from previous selection
                if(((SudokuViewAdapter) sudokuGridView.getAdapter()).getSelectedPosition()!=-1)
                    setCellColor(((SudokuViewAdapter) sudokuGridView.getAdapter()).getSelectedPosition(),CLEAN);
                if(((SudokuViewAdapter) sudokuGridView.getAdapter()).getSelectedPosition()==position){
                    Log.i(TAG,"Reselected the cell that are selected, clearing selected");
                    ((SudokuViewAdapter) sudokuGridView.getAdapter()).setSelectedPosition(-1, INVALID);
                    setCellColor(position,CLEAN);
                }else {
                    //Coloring horizontal and vertical cells orange
                    ((SudokuViewAdapter) sudokuGridView.getAdapter()).setSelectedPosition(position, ANSWER);
                    setCellColor(position, ANSWER);
                }
            } else{
                Log.i(TAG,"Item is inactive");
            }

        });
    }

    private void initOnItemLongClickListener(){
        sudokuGridView.setOnItemLongClickListener((parent, view, position, id) -> {
            Log.i(TAG,"Long click with position "+position);
            if(((SudokuViewAdapter)sudokuGridView.getAdapter()).isItemActive(position)||creator_mode) {
                Log.i(TAG, "Item is active");
                Log.d(TAG,"previous selected item position: "+ ((SudokuViewAdapter) sudokuGridView.getAdapter()).getSelectedPosition());

                //Removing color from previous selection
                if(((SudokuViewAdapter) sudokuGridView.getAdapter()).getSelectedPosition()!=-1)
                    setCellColor(((SudokuViewAdapter) sudokuGridView.getAdapter()).getSelectedPosition(),CLEAN);

                //coloring horizontal and vertical cells blue
                ((SudokuViewAdapter) sudokuGridView.getAdapter()).setSelectedPosition(position,THINK);
                setCellColor(position,THINK);
                return true;
            } else{
                Log.i(TAG,"Item is inactive");
                return false;
            }

        });
    }

    private void setCellColor(int position,int answerMode){
        int mainColorId = -1;
        int subColorId = -1;
        if(answerMode==ANSWER){//Orange will indicate answerMode
            Log.i(TAG,"Entering answering mode at position: "+position);
            mainColorId = getResources().getColor(R.color.light_orange,null);
            subColorId = getResources().getColor(R.color.orange,null);

        }else if(answerMode==THINK){//Blue will indicate thinkingMode
            Log.i(TAG,"Entering thinking mode at position: "+position);
            mainColorId = getResources().getColor(R.color.teal_700,null);
            subColorId = getResources().getColor(R.color.teal_200,null);

        }else if(answerMode==CLEAN){
            Log.i(TAG,"Entering cleanup mode at position: "+position);
            mainColorId = getResources().getColor(R.color.white,null);
            subColorId = getResources().getColor(R.color.white,null);

        }else{
            Log.e(TAG,"Illegal answer mode");
        }


        int horizontalPointer = position-position%9;//Resetting to the cell all the way to the left
        for(int i = 0;i<9;i++){//moving right of the selected tile
            ConstraintLayout cell = (ConstraintLayout)sudokuGridView.getChildAt(horizontalPointer+i);
            cell.setBackgroundColor(subColorId);
        }
        int verticalPointer=position;
        while(verticalPointer-9>=0){
            verticalPointer-=9;//Move from position to top of board
            ConstraintLayout cell = (ConstraintLayout)sudokuGridView.getChildAt(verticalPointer);
            cell.setBackgroundColor(subColorId);
        }
        verticalPointer=position;
        while(verticalPointer+9<81){
            verticalPointer+=9;//Move from position to bottom of board
            ConstraintLayout cell = (ConstraintLayout)sudokuGridView.getChildAt(verticalPointer);
            cell.setBackgroundColor(subColorId);
        }
        ConstraintLayout cell = (ConstraintLayout)sudokuGridView.getChildAt(position);
        cell.setBackgroundColor(mainColorId);
    }

    //Collecting all the button inits for cleaner solution
    private void initAllButtons(){
        initButton1();
        initButton2();
        initButton3();
        initButton4();
        initButton5();
        initButton6();
        initButton7();
        initButton8();
        initButton9();
    }

    private void buttonHandler(String number){
        ConstraintLayout cell = (ConstraintLayout)sudokuGridView.getChildAt(
                ((SudokuViewAdapter) sudokuGridView.getAdapter()).getSelectedPosition());
        if(((SudokuViewAdapter)sudokuGridView.getAdapter()).getSelectedMode()==THINK){
            Log.i(TAG,"Editing cell in think mode");
            TextView editTextView = cell.findViewById(
                    getResources().getIdentifier("possibility_"+number,"id",getPackageName()));

            if(editTextView.getTextColors().equals(getColorStateList(getResources().getIdentifier("gray","color",getPackageName())))){
                Log.i(TAG,"The textView is gray");
                editTextView.setTextColor(getColorStateList(getResources().getIdentifier("black","color",getPackageName())));

            }else if(editTextView.getTextColors().equals(getColorStateList(getResources().getIdentifier("black","color",getPackageName())))){
                Log.i(TAG,"The textView is black");
                editTextView.setTextColor(getColorStateList(getResources().getIdentifier("gray","color",getPackageName())));

            }else{
                Log.e(TAG,"The textView is an unknown color");

            }

        }else if(((SudokuViewAdapter)sudokuGridView.getAdapter()).getSelectedMode()==ANSWER){
            Log.i(TAG,"Editing cell in answer mode");
            TextView answerTextView= cell.findViewById(R.id.answer);
            ConstraintLayout possibilityGrid = cell.findViewById(R.id.possibilities_grid);
            if(answerTextView.getText().equals("")){
                Log.i(TAG,"Editing an unanswered cell");
                answerTextView.setText(number);
                if(creator_mode){
                    ((SudokuViewAdapter)sudokuGridView.getAdapter()).setHint(((SudokuViewAdapter)sudokuGridView.getAdapter()).getSelectedPosition(),number);
                    answerTextView.setTextAppearance(R.style.Default_font);
                }else {
                    answerTextView.setTextAppearance(R.style.Casual_font);

                }
                ((SudokuViewAdapter)sudokuGridView.getAdapter()).setCell(((SudokuViewAdapter)sudokuGridView.getAdapter()).getSelectedPosition(),number);
                possibilityGrid.setVisibility(TextView.INVISIBLE);
            }else{
                Log.i(TAG,"Editing an answered cell");
                if(answerTextView.getText().equals(number)){
                    Log.i(TAG,"Same number selected, deleting field");
                    answerTextView.setText("");
                    if(creator_mode){
                        ((SudokuViewAdapter)sudokuGridView.getAdapter()).setHint(((SudokuViewAdapter)sudokuGridView.getAdapter()).getSelectedPosition(),"");
                    }
                    ((SudokuViewAdapter)sudokuGridView.getAdapter()).setCell(((SudokuViewAdapter)sudokuGridView.getAdapter()).getSelectedPosition(),"");
                    possibilityGrid.setVisibility(TextView.VISIBLE);

                }else{
                    Log.i(TAG,"Different number selected, replacing field");
                    answerTextView.setText(number);
                    if(creator_mode){
                        ((SudokuViewAdapter)sudokuGridView.getAdapter()).setHint(((SudokuViewAdapter)sudokuGridView.getAdapter()).getSelectedPosition(),number);
                        answerTextView.setTextAppearance(R.style.Default_font);
                    }else{
                        answerTextView.setTextAppearance(R.style.Casual_font);
                    }
                    ((SudokuViewAdapter)sudokuGridView.getAdapter()).setCell(((SudokuViewAdapter)sudokuGridView.getAdapter()).getSelectedPosition(),number);
                }
            }
            checkCorrectFromPosition(((SudokuViewAdapter) sudokuGridView.getAdapter()).getSelectedPosition());
            checkIncorrect();
            Log.d(TAG,"The number of incorrect cells: "+incorrect.size()+". There are no more empty cells: " + ((SudokuViewAdapter)sudokuGridView.getAdapter()).isNoEmpty());
            if(incorrect.size()==0&&((SudokuViewAdapter)sudokuGridView.getAdapter()).isNoEmpty()&&!creator_mode){
                showVictoryScreen();
            }


        }else if(((SudokuViewAdapter)sudokuGridView.getAdapter()).getSelectedMode()==INVALID){
            Log.i(TAG,"Button pressed without selecting a cell");

            Toast toast = Toast.makeText(getApplicationContext(),
                    getResources().getText(R.string.toast_no_selected_cell),Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    private void checkIncorrect(){
        for(int i =0; i<incorrect.size();i++){
            TextView view = sudokuGridView.getChildAt(incorrect.get(i)).findViewById(R.id.answer);


            if(!view.getText().toString().equals("")){
                boolean isCorrect = true;

                //Horizontal search
                int horizontalPointer=incorrect.get(i)-incorrect.get(i)%9;
                for(int horizontalSearch=0;horizontalSearch<9;horizontalSearch++){
                    if(horizontalPointer+horizontalSearch!=incorrect.get(i)) {
                        TextView checkView = sudokuGridView.getChildAt(horizontalPointer + horizontalSearch).findViewById(R.id.answer);
                        if (checkView.getText().equals(view.getText())){
                            isCorrect=false;
                            break;
                        }
                    }
                }
                if(!isCorrect) continue;

                //Vertical checker
                int verticalPointer=incorrect.get(i);
                while(verticalPointer-9>=0){
                    verticalPointer-=9;
                    TextView checkView=sudokuGridView.getChildAt(verticalPointer).findViewById(R.id.answer);
                    if(checkView.getText().equals(view.getText())){
                        isCorrect=false;
                        break;
                    }
                }


                if(!isCorrect) continue;

                verticalPointer=incorrect.get(i);
                while(verticalPointer+9<81){
                    verticalPointer+=9;
                    TextView checkView = sudokuGridView.getChildAt(verticalPointer).findViewById(R.id.answer);
                    if(checkView.getText().equals(view.getText())){
                        isCorrect=false;
                        break;
                    }
                }

                if(!isCorrect) continue;

                verticalPointer=incorrect.get(i)-incorrect.get(i)%27;
                for (int verticalScanner=0;verticalScanner<3;verticalScanner++){
                    horizontalPointer=verticalPointer+verticalScanner*9;
                    int gridSpace = (incorrect.get(i)%9/3)*3;
                    for(int horisontalScanner=gridSpace;horisontalScanner<gridSpace+3;horisontalScanner++){
                        if(horizontalPointer+horisontalScanner!=incorrect.get(i)) {
                            TextView checkView = sudokuGridView.getChildAt(horisontalScanner+horizontalPointer).findViewById(R.id.answer);
                            if(checkView.getText().equals(view.getText())){
                                isCorrect=false;
                                break;
                            }
                        }

                    }
                    if(!isCorrect)break;
                }
                if(!isCorrect) continue;

                //resetting..
                Log.i(TAG,"Resetting "+incorrect.get(i)+" since it passed all the tests");

            }else{
                Log.i(TAG,"Resetting "+incorrect.get(i)+" for not having anything in it");

            }
            if (((SudokuViewAdapter)sudokuGridView.getAdapter()).isItemActive(incorrect.get(i)))
                view.setTextColor(getResources().getColor(R.color.purple_500,null));
            else
                view.setTextColor(getResources().getColor(R.color.black,null));
            incorrect.remove(i);
            i--;
        }

    }
    private void showVictoryScreen(){
        Log.i(TAG,"player has won");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.victory_screen,VictoryScreenFragment.newInstance(((SudokuViewAdapter)sudokuGridView.getAdapter()).getHints()));
        if(getIntent().getBooleanExtra(CREATOR_MODE,false)){
            Log.d(TAG,"Creator mode enabled");
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    private void checkCorrectFromPosition(int position){
        //This code is kinda lengthy...
        //Horizontal check
        int horizontalPointer=position-position%9;
        int[] found = new int[9];
        for(int i =0;i<9;i++){
            TextView view = sudokuGridView.getChildAt(horizontalPointer+i).findViewById(R.id.answer);

            if(!view.getText().toString().equals("")) {
                int answered_integer = Integer.parseInt(view.getText().toString())-1;

                if (found[answered_integer]==0){
                    //shift position with one to allow 0 to be the "not found" value
                    found[answered_integer]=horizontalPointer+i+1;

                }else{
                    Log.i(TAG,"Found horizontal errors at "+(found[answered_integer]-1)+" and "+ (horizontalPointer+i));
                    TextView misplaced = sudokuGridView.getChildAt(found[answered_integer]-1).findViewById(R.id.answer);
                    misplaced.setTextColor(getResources().getColor(R.color.red,null));
                    if(!incorrect.contains(found[answered_integer]-1))incorrect.add(found[answered_integer]-1);
                    view.setTextColor(getResources().getColor(R.color.red,null));
                    if(!incorrect.contains(horizontalPointer+i))incorrect.add(horizontalPointer+i);

                }
            }
        }

        found = new int[9];
        //Add self
        TextView selected = sudokuGridView.getChildAt(position).findViewById(R.id.answer);
        if(!selected.getText().toString().equals("")){
            int answered_integer = Integer.parseInt(selected.getText().toString())-1;
            found[answered_integer]=position+1;
        }
        //Vertical check upwards
        int verticalPointer = position;

        while(verticalPointer-9>=0){
            verticalPointer-=9;
            TextView view = sudokuGridView.getChildAt(verticalPointer).findViewById(R.id.answer);
            if(!view.getText().toString().equals("")) {
                int answered_integer = Integer.parseInt(view.getText().toString())-1;

                if (found[answered_integer]==0){
                    //shift position with one to allow 0 to be the "not found" value
                    found[answered_integer]=verticalPointer+1;

                }else{
                    Log.i(TAG,"Found Vertical upwards errors at "+(found[answered_integer]-1)+" and "+ verticalPointer);
                    TextView misplaced = sudokuGridView.getChildAt(found[answered_integer]-1).findViewById(R.id.answer);
                    misplaced.setTextColor(getResources().getColor(R.color.red,null));
                    if(!incorrect.contains(found[answered_integer]-1))incorrect.add(found[answered_integer]-1);
                    view.setTextColor(getResources().getColor(R.color.red,null));
                    if(!incorrect.contains(verticalPointer))incorrect.add(verticalPointer);

                }
            }
        }
        //Vertical check downwards
        verticalPointer=position;
        while(verticalPointer+9<81){
            verticalPointer+=9;
            TextView view = sudokuGridView.getChildAt(verticalPointer).findViewById(R.id.answer);
            if(!view.getText().toString().equals("")) {
                int answered_integer = Integer.parseInt(view.getText().toString())-1;

                if (found[answered_integer]==0){
                    //shift position with one to allow 0 to be the "not found" value
                    found[answered_integer]=verticalPointer+1;

                }else{
                    Log.i(TAG,"Found Vertical downwards errors at "+(found[answered_integer]-1)+" and "+ verticalPointer);
                    TextView misplaced = sudokuGridView.getChildAt(found[answered_integer]-1).findViewById(R.id.answer);
                    misplaced.setTextColor(getResources().getColor(R.color.red,null));
                    if(!incorrect.contains(found[answered_integer]-1))incorrect.add(found[answered_integer]-1);
                    view.setTextColor(getResources().getColor(R.color.red,null));
                    if(!incorrect.contains(verticalPointer))incorrect.add(verticalPointer);

                }
            }
        }

        //Grid check, current horizontal
        //Recreating pos = (start-mod27)+div9*9+div3*3+mod3
        found = new int[9];
        int mod27 = position%27;
        int mod9 = position%9;
        int div3=mod9/3;

        verticalPointer = position-mod27;
        for(int j=0;j<3;j++) {
            horizontalPointer = verticalPointer +j*9;
            for (int i = div3*3; i < div3*3+3; i++) {
                TextView view = sudokuGridView.getChildAt(horizontalPointer + i).findViewById(R.id.answer);
                if (!view.getText().toString().equals("")) {
                    int answered_integer = Integer.parseInt(view.getText().toString()) - 1;
                    if (found[answered_integer] == 0) {
                        //shift position with one to allow 0 to be the "not found" value
                        found[answered_integer] = horizontalPointer + i + 1;

                    } else {
                        Log.i(TAG, "Found horizontal grid errors at " + (found[answered_integer] - 1) + " and " + (horizontalPointer + i));
                        TextView misplaced = sudokuGridView.getChildAt(found[answered_integer] - 1).findViewById(R.id.answer);
                        misplaced.setTextColor(getResources().getColor(R.color.red, null));
                        if (!incorrect.contains(found[answered_integer] - 1)) incorrect.add(found[answered_integer] - 1);
                        view.setTextColor(getResources().getColor(R.color.red, null));
                        if (!incorrect.contains(horizontalPointer + i)) incorrect.add(horizontalPointer + i);

                    }
                }
            }
        }


    }


    private void initButton1(){
        button1=findViewById(R.id.button_1);
        button1.setOnClickListener(v -> {
            Log.i(TAG,"Button 1 pressed");
            buttonHandler("1");
        });
    }
    private void initButton2(){
        button2=findViewById(R.id.button_2);
        button2.setOnClickListener(v -> {
            Log.i(TAG,"Button 2 pressed");
            buttonHandler("2");
        });
    }

    private void initButton3(){
        button3=findViewById(R.id.button_3);
        button3.setOnClickListener(v -> {
            Log.i(TAG,"Button 3 pressed");
            buttonHandler("3");
        });
    }

    private void initButton4(){
        button4=findViewById(R.id.button_4);
        button4.setOnClickListener(v -> {
            Log.i(TAG,"Button 4 pressed");
            buttonHandler("4");
        });
    }
    private void initButton5(){
        button5=findViewById(R.id.button_5);
        button5.setOnClickListener(v -> {
            Log.i(TAG,"Button 5 pressed");
            buttonHandler("5");
        });
    }
    private void initButton6(){
        button6=findViewById(R.id.button_6);
        button6.setOnClickListener(v -> {
            Log.i(TAG,"Button 6 pressed");
            buttonHandler("6");
        });
    }
    private void initButton7(){
        button7=findViewById(R.id.button_7);
        button7.setOnClickListener(v -> {
            Log.i(TAG,"Button 7 pressed");
            buttonHandler("7");
        });
    }
    private void initButton8(){
        button8=findViewById(R.id.button_8);
        button8.setOnClickListener(v -> {
            Log.i(TAG,"Button 8 pressed");
            buttonHandler("8");
        });
    }

    private void initButton9(){
        button9=findViewById(R.id.button_9);
        button9.setOnClickListener(v -> {
            Log.i(TAG,"Button 9 pressed");
            buttonHandler("9");
        });
    }

}