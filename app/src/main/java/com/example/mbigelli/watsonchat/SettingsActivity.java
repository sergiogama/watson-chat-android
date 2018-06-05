package com.example.mbigelli.watsonchat;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary("");

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || SpeechRecognitionFragment.class.getName().equals(fragmentName)
                || OrchestrationFragment.class.getName().equals(fragmentName)
                || VoiceSynthesisFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SpeechRecognitionFragment extends PreferenceFragment {

        SwitchPreference useSpeechToTextSwitch;
        SwitchPreference useNativeSwitch;
        EditTextPreference customURLEdit;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_speech_recognition);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("speech_recognition_username"));
            bindPreferenceSummaryToValue(findPreference("speech_recognition_password"));
            bindPreferenceSummaryToValue(findPreference("speech_recognition_language"));
            bindPreferenceSummaryToValue(findPreference("speech_recognition_custom_url"));

            // Make switches dependent
            useSpeechToTextSwitch = (SwitchPreference) findPreference("speech_recognition_use_speech_to_text");
            useNativeSwitch = (SwitchPreference) findPreference("speech_recognition_use_native");
            customURLEdit = (EditTextPreference) findPreference("speech_recognition_custom_url");

            useSpeechToTextSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (useSpeechToTextSwitch.isChecked()) {
                        useNativeSwitch.setChecked(false);
                        customURLEdit.setEnabled(false);
                    } else {
                        customURLEdit.setEnabled(true);
                    }
                    return false;
                }
            });
            useNativeSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (useNativeSwitch.isChecked()) {
                        useSpeechToTextSwitch.setChecked(false);
                        customURLEdit.setEnabled(false);
                    } else {
                        customURLEdit.setEnabled(true);
                    }
                    return false;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class VoiceSynthesisFragment extends PreferenceFragment {

        SwitchPreference useTextToSpeechSwitch;
        SwitchPreference useNativeSwitch;
        ListPreference voice;
        EditTextPreference customURLEdit;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_voice_synthesis);
            setHasOptionsMenu(true);

            useTextToSpeechSwitch = (SwitchPreference) findPreference("voice_synthesis_use_text_to_speech");
            useNativeSwitch = (SwitchPreference) findPreference("voice_synthesis_use_native");
            voice = (ListPreference) findPreference("voice_synthesis_voice");
            customURLEdit = (EditTextPreference) findPreference("voice_synthesis_custom_url");

            bindPreferenceSummaryToValue(findPreference("voice_synthesis_username"));
            bindPreferenceSummaryToValue(findPreference("voice_synthesis_password"));
            bindPreferenceSummaryToValue(voice);
            bindPreferenceSummaryToValue(customURLEdit);

            // Make switches dependent
            useTextToSpeechSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (useTextToSpeechSwitch.isChecked()) {
                        useNativeSwitch.setChecked(false);
                        customURLEdit.setEnabled(false);
                    } else {
                        customURLEdit.setEnabled(true);
                    }
                    return false;
                }
            });
            useNativeSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (useNativeSwitch.isChecked()) {
                        useTextToSpeechSwitch.setChecked(false);
                        customURLEdit.setEnabled(false);
                    } else {
                        customURLEdit.setEnabled(true);
                    }
                    return false;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class OrchestrationFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_orchestration);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("orchestration_username"));
            bindPreferenceSummaryToValue(findPreference("orchestration_password"));
            bindPreferenceSummaryToValue(findPreference("orchestration_workspace"));
            bindPreferenceSummaryToValue(findPreference("orchestration_custom_url"));

            // Make fields dependent - can't do both via xml
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            findPreference("orchestration_custom_url").setEnabled(!prefs.getBoolean("orchestration_use_conversation", false));
            findPreference("orchestration_workspace").setEnabled(prefs.getBoolean("orchestration_use_conversation", true));
            findPreference("orchestration_use_conversation").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    findPreference("orchestration_custom_url").setEnabled(!findPreference("orchestration_custom_url").isEnabled());
                    findPreference("orchestration_workspace").setEnabled(!findPreference("orchestration_workspace").isEnabled());
                    return false;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
