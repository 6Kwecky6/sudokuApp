package com.kevinah.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final static String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting configurations to work
        Configuration newConfiguration=getCustomConfiguration();
        getResources().updateConfiguration(newConfiguration,getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main);
        Log.i(TAG,"Starting application");
        Log.d(TAG,"Device SDK: "+Build.VERSION.SDK_INT);
        Log.d(TAG,"Device locale: "+getResources().getConfiguration().locale);
        initPlayExistingButton();
        initCreateNewBoard();
        initSettingsButton();
        initHelpButton();
        initInformationButton();
    }

    private void initHelpButton(){
        Button helpButton=findViewById(R.id.help_button_main);
        helpButton.setOnClickListener(v->{
            Log.i(TAG,"Help button pressed");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_menu,HelpFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }


    //Function to load custom preferences before we start MainActivity
    private Configuration getCustomConfiguration(){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
        Configuration config = getResources().getConfiguration();

        //This loads language preference
        if(preferences.contains("preference_language")){
            Locale newLocale;
            Log.i(TAG,"Language preference found, using preferred language");
            switch (preferences.getString("preference_language","noPreference")) {
                case "Norsk-bokmål":
                    newLocale = new Locale("no");
                    Locale.setDefault(newLocale);
                    config.setLocale(newLocale);
                    break;
                case "English":
                    newLocale = new Locale("en_GB");
                    Locale.setDefault(newLocale);
                    config.setLocale(newLocale);
                    break;
                case "noPreference":
                    Log.e(TAG,"Could not find the preference even though we just confirmed that");
                    break;
                default:
                    Log.e(TAG,"Selected a non supported language");
            }

        }else{
            Log.i(TAG,"No persistent storage on prefered language");
        }

        return config;
    }

    private void initSettingsButton(){
        //initialize settings button and create an onClickListener
        Button settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> {
            Log.i(TAG,"Settings button clicked");
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.fragment_menu,SettingsFragment.newInstance()).addToBackStack(null);
            transaction.commit();
        });
    }
    private void initCreateNewBoard(){
        Button createNewButton = findViewById(R.id.make_new_board_button);
        createNewButton.setOnClickListener(v->{
            Log.i(TAG,"New board button clicked");
            Intent intent = new Intent(this,SudokuActivity.class);
            intent.putExtra(SudokuActivity.CREATOR_MODE,true);
            startActivity(intent);
        });

    }

    private void initPlayExistingButton(){
        //initialize playExisting button and create an onClickListener
        Button playExistingButton = findViewById(R.id.play_existing_button);
        playExistingButton.setOnClickListener(v -> {
            Log.i(TAG,"Play existing button clicked");
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.fragment_menu,ChooseBoardFragment.newInstance()).addToBackStack(null);
            transaction.commit();

        });
    }

    private void initInformationButton(){
        Button informationButton=findViewById(R.id.information_button);
        informationButton.setOnClickListener(v->{
            Log.i(TAG,"InformationButton pressed");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_menu,InformationFragment.newInstance()).addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

}