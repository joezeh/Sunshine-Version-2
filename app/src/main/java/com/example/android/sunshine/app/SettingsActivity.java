package com.example.android.sunshine.app;


import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{

    public static final String KEY_PREF_ZIP = "ZIP_CODE_PREF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        System.out.println("Hit it");
        updatePreference(key);
    }

    public void updatePreference(String key){
        if (key.equals(KEY_PREF_ZIP)){
            Preference preference = findPreference(key);
            if (preference instanceof EditTextPreference){
                EditTextPreference editTextPreference = (EditTextPreference)preference;
                if (editTextPreference.getText().trim().length()>0){
                    System.out.println("Update Ran");
                    editTextPreference.setSummary("Current Zip Code: "+ editTextPreference);}
                else{
                    System.out.println("Update didn't run :(");
                    editTextPreference.setSummary(editTextPreference.getSummary());
                }
                }
            }
        }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName);
    }


}
