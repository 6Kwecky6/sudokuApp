package com.kevinah.sudoku;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Locale;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    private final static String TAG = "SettingsFragment";
    private final static String GET_LANGUAGE = "preference_language";
    private final static String GET_CATEGORY_LANGUAGE ="language_category";
    private final static String ENGLISH = "English";
    private final static String NORWEGIAN = "Norsk-bokmål";

    public static SettingsFragment newInstance(){//using this static method for consistency sake
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Log.i(TAG,"onPreferenceCreated called");
        Log.d(TAG,"savedInstanceState: " +savedInstanceState+ "\nrootKey: "+rootKey);
        setPreferencesFromResource(R.xml.settings_screen,null);
        initLangPreference();

    }

    private void initLangPreference(){
        ListPreference langPreference = Objects.requireNonNull(findPreference(GET_LANGUAGE));
        Locale deviceLocale=getResources().getConfiguration().locale;
        if(langPreference.getValue()==null) {
            switch (deviceLocale.toString()) {
                case "nn_NO"://Har alleformene for norsk for å fange opp begge tilfellene. TODO: Nynorsk
                    Log.i(TAG, "Norsk-Nynorsk detected");
                    langPreference.setValue(NORWEGIAN);//Sorry for now Nynorsk users...
                    break;
                case "nb_NO":
                    Log.i(TAG, "Norsk-Bokmål detected, transitioning to norsk");
                case "no_NO":
                    Log.i(TAG, "Norsk detected");
                    langPreference.setValue(NORWEGIAN);
                    break;
                case "en_GB":
                    Log.i(TAG, "English-UK detected");
                    langPreference.setValue(ENGLISH);
                    break;
                default:
                    Log.i(TAG, "Locale doesn't match with any translations, resorting to default");
                    langPreference.setValue(ENGLISH);
                    break;
            }
        }else{
            Log.i(TAG,"Persistent storage found for language preference");
        }

        langPreference.setOnPreferenceChangeListener(((preference, newValue) -> {

            Locale newLocale;
            if(newValue.equals(ENGLISH)){
                Log.i(TAG,"Selected English language");
                newLocale = new Locale("en");
            }else if(newValue.equals(NORWEGIAN)){
                Log.i(TAG,"Selected Norwegian language");
                newLocale =new Locale("no");
            }else{
                Log.e(TAG,"Illegal preference selected");
                newLocale=new Locale("en");
            }
            Configuration configuration = new Configuration();
            configuration.locale=newLocale;
            Resources res = requireActivity().getBaseContext().getResources();
            res.updateConfiguration(configuration,res.getDisplayMetrics());
            langPreference.setTitle(R.string.listPreference_language_selection);
            PreferenceCategory languageCategory = Objects.requireNonNull(findPreference(GET_CATEGORY_LANGUAGE));
            languageCategory.setTitle(R.string.header_settings);
            if(requireActivity() instanceof MainActivity){
                updateMainActivity((MainActivity)requireActivity());

            }else if(requireActivity() instanceof SudokuActivity){
                updateSudokuActivity((SudokuActivity)requireActivity());

            }else{
                Log.e(TAG,"Illegal activity");
            }
            return true;
        }));

    }

    private void updateMainActivity(MainActivity activity){
        Log.i(TAG,"Updating main activity text");
        ((TextView)activity.findViewById(R.id.main_page_title)).setText(R.string.label_home_title);
        ((Button)activity.findViewById(R.id.play_existing_button)).setText(R.string.button_play_existing);
        ((Button)activity.findViewById(R.id.make_new_board_button)).setText(R.string.button_create_board);
        ((Button)activity.findViewById(R.id.help_button_main)).setText(R.string.button_help);
        ((Button)activity.findViewById(R.id.settings_button)).setText(R.string.button_settings);
        ((Button)activity.findViewById(R.id.information_button)).setText(R.string.button_info);

    }

    private void updateSudokuActivity(SudokuActivity activity){
        Log.i(TAG,"Updating sudoku activity text");
        MenuItem toggleItem = activity.getTogglePlayMode();
        if(activity.isCreatorMode()){
            toggleItem.setTitle(R.string.action_toggle_creator_mode);
        }else{
            toggleItem.setTitle(R.string.action_toggle_player_mode);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =Objects.requireNonNull(super.onCreateView(inflater, container, savedInstanceState));
        view.setBackgroundColor(getResources().getColor(R.color.purple_menu,null));
        return view;
    }
}
