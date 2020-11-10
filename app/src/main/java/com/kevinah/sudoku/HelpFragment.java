package com.kevinah.sudoku;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpFragment extends Fragment {
    private static final String GET_PAGE = "Page";
    private static final String TAG="HelpFragment";
    private static final int LAST_PAGE = 5;
    private int page;
    private Button leftButton,rightButton;
    private TextView pageTextView,contentTextView,titleTextView;
    private ImageView helpImage;


    public HelpFragment() {
        // Required empty public constructor
    }

    public static HelpFragment newInstance() {
        HelpFragment fragment = new HelpFragment();
        Bundle args = new Bundle();
        args.putInt(GET_PAGE,1);
        fragment.setArguments(args);
        return fragment;
    }

    //Allowing to call help fragment at a certain page for more ease of accessibility
    public static HelpFragment newInstance(int page) {
        HelpFragment fragment = new HelpFragment();
        Bundle args = new Bundle();
        args.putInt(GET_PAGE,page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            Log.i(TAG,"Starting help fragment with arguments " + getArguments().toString());
            page = getArguments().getInt(GET_PAGE);
            if(page>LAST_PAGE||page<1){
                Log.i(TAG,"Got an invalid page number");
                page=1;
            }
        }else{
            Log.d(TAG, "no arguments were found");
        }

    }

    private void setHelpValues(){
        Log.i(TAG, "Handling page " +page);
        if(page==1){
            Log.i(TAG,"This is the first page, disabling left button");
            leftButton.setEnabled(false);
            leftButton.setBackgroundColor(getResources().getColor(R.color.gray,null));
        }else{
            leftButton.setEnabled(true);
            leftButton.setBackgroundColor(getResources().getColor(R.color.purple_200,null));
        }
        if(page==LAST_PAGE){
            Log.i(TAG,"this is the last page, disabling right button");
            rightButton.setEnabled(false);
            rightButton.setBackgroundColor(getResources().getColor(R.color.gray,null));
        }else {
            rightButton.setEnabled(true);
            rightButton.setBackgroundColor(getResources().getColor(R.color.purple_200,null));
        }
        pageTextView.setText(getResources().getString(R.string.label_page_number,page,LAST_PAGE));

        switch (page){
            case 1:
                contentTextView.setText(R.string.label_help_text_page_1);

            case 2:
                if(page==2)contentTextView.setText(R.string.label_help_text_page_2);
                helpImage.setImageResource(R.drawable.main_board);
            case 3:
                if(page==3) {
                    contentTextView.setText(R.string.label_help_text_page_3);
                    helpImage.setImageResource(R.drawable.main_board_think_mode);
                }
                titleTextView.setText(R.string.label_help_title_how_to);
                break;
            case 4:
                contentTextView.setText(R.string.label_help_text_page_4);
                helpImage.setImageResource(R.drawable.creator_mode);
            case 5:
                if(page==5) {
                    contentTextView.setText(R.string.label_help_text_page_5);
                    helpImage.setImageResource(R.drawable.player_mode);
                }
                titleTextView.setText(R.string.label_help_title_creator_mode);
                break;
            default:
                Log.e(TAG,"Not a valid page");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        initBackButton(rootView);
        initLeftButton(rootView);
        initRightButton(rootView);
        initHelpViews(rootView);
        setHelpValues();
        return rootView;
    }

    private void initHelpViews(View rootView){
        pageTextView = rootView.findViewById(R.id.page_counter);
        contentTextView = rootView.findViewById(R.id.help_text);
        titleTextView = rootView.findViewById(R.id.help_title);
        helpImage = rootView.findViewById(R.id.help_image);
    }

    private void initLeftButton(View rootView){
        leftButton = rootView.findViewById(R.id.left_button_help);
        leftButton.setOnClickListener(v->{
            Log.i(TAG,"Left button pressed");
            page--;
            setHelpValues();
        });
    }

    private void initRightButton(View rootView){
        rightButton = rootView.findViewById(R.id.right_button_help);
        rightButton.setOnClickListener(v->{
            Log.i(TAG,"Right button pressed");
            page++;
            setHelpValues();
        });
    }

    private void initBackButton(View rootView){

        Button backButton = rootView.findViewById(R.id.back_button_help);
        backButton.setOnClickListener(v->{
            Log.i(TAG,"Back button pressed");
            getParentFragmentManager().beginTransaction().remove(HelpFragment.this).commit();
            getParentFragmentManager().popBackStack();
        });
    }
}