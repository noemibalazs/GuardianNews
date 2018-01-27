package com.example.android.guardiannews;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsSharedPreference extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_settings);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummeryToValue(orderBy);

            Preference search = findPreference(getString(R.string.search_key));
            bindPreferenceSummeryToValue(search);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex =listPreference.findIndexOfValue(stringValue);
                if (prefIndex>0){
                    CharSequence [] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }
            else
                { preference.setSummary(stringValue);}
            return true;
        }

        private void bindPreferenceSummeryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String prefString = sharedPreferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, prefString);
        }
    }
}
