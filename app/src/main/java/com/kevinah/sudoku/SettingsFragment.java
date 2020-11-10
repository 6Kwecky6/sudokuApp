package com.kevinah.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Locale;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    private final static String TAG = "SettingsFragment";

    public static SettingsFragment newInstance(){//using this static method for consistency sake
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Log.i(TAG,"onPreferenceCreated called");
        Log.d(TAG,"savedInstanceState: " +savedInstanceState+ "\nrootKey: "+rootKey);
        setPreferencesFromResource(R.xml.settings_screen,rootKey);
        initLangPreference();

    }

    private void initLangPreference(){
        ListPreference langPreference = Objects.requireNonNull(findPreference("preference_language"));
        Locale deviceLocale=getResources().getConfiguration().locale;
        if(langPreference.getValue()==null) {
            switch (deviceLocale.toString()) {
                case "nn_NO"://Har alleformene for norsk for å fange opp begge tilfellene. TODO: Nynorsk
                    Log.i(TAG, "Norsk-Nynorsk detected");
                    langPreference.setValue("Norsk-bokmål");//Sorry for now Nynorsk users...
                    break;
                case "nb_NO":
                    Log.i(TAG, "Norsk-Bokmål detected, transitioning to norsk");
                case "no_NO":
                    Log.i(TAG, "Norsk detected");
                    langPreference.setValue("Norsk-bokmål");
                    break;
                case "en_GB":
                    Log.i(TAG, "English-UK detected");
                    langPreference.setValue("English");
                    break;
                default:
                    Log.i(TAG, "Locale doesn't match with any translations, resorting to default");
                    langPreference.setValue("English");
                    break;
            }
        }else{
            Log.i(TAG,"Persistent storage found for language preference");
        }
        langPreference.setOnPreferenceChangeListener(((preference, newValue) -> {
            Log.i(TAG,"Selected language "+newValue);
            Intent refresh = new Intent(requireActivity(),MainActivity.class);
            requireActivity().finish();
            startActivity(refresh);
            return true;
        }));

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =Objects.requireNonNull(super.onCreateView(inflater, container, savedInstanceState));
        view.setBackgroundColor(getResources().getColor(R.color.purple_menu,null));
        return view;
    }
}
