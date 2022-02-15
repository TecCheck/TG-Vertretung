package de.coop.tgvertretung.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsWrapper {
    // Visible in SettingsActivity
    private static final String KEY_FILTER_ENABLED = "filter_enabled";
    private static final String KEY_FILTER = "filter";
    private static final String KEY_SHOW_AB = "show_ab";
    private static final String KEY_EXTENDED_VIEW = "extended_view";
    private static final String KEY_SHOW_TEXT = "show_text";
    private static final String KEY_TWO_LINE_LABEL = "two_line_label";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_SHOW_SERVER_REFRESH = "show_server_refresh";
    private static final String KEY_SHOW_CLIENT_REFRESH = "show_client_refresh";
    private static final String KEY_RELATIVE_TIME = "relative_time";
    private static final String KEY_RAINBOW = "rainbow";

    // Not visible in SettingsActivity
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LAST_CLIENT_REFRESH = "last_client_refresh";

    private final SharedPreferences sharedPreferences;

    public SettingsWrapper(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_preferences", 0);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.contains(KEY_USERNAME) && sharedPreferences.contains(KEY_PASSWORD);
    }

    public boolean getFilterEnabled() {
        return sharedPreferences.getBoolean(KEY_FILTER_ENABLED, false);
    }

    public String getFilter() {
        return sharedPreferences.getString(KEY_FILTER, "");
    }

    public boolean getShowAb() {
        return sharedPreferences.getBoolean(KEY_SHOW_AB, true);
    }

    public boolean getExtended() {
        return sharedPreferences.getBoolean(KEY_EXTENDED_VIEW, false);
    }

    public boolean getShowText() {
        return sharedPreferences.getBoolean(KEY_SHOW_TEXT, true);
    }

    public boolean getTwoLineLabel() {
        return sharedPreferences.getBoolean(KEY_TWO_LINE_LABEL, false);
    }

    public int getThemeMode() {
        // Why? Why can't I use ints with a list preference?
        switch (sharedPreferences.getString(KEY_THEME_MODE, "")) {
            case "light":
                return 1;
            case "dark":
                return 2;
            default:
                return 0;
        }
    }

    public boolean getShowServerRefresh() {
        return sharedPreferences.getBoolean(KEY_SHOW_SERVER_REFRESH, true);
    }

    public boolean getShowClientRefresh() {
        return sharedPreferences.getBoolean(KEY_SHOW_CLIENT_REFRESH, true);
    }

    public boolean getShowRelativeTime() {
        return sharedPreferences.getBoolean(KEY_RELATIVE_TIME, true);
    }

    public boolean getRainbow() {
        return sharedPreferences.getBoolean(KEY_RAINBOW, false);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, "");
    }

    public long getLastClientRefresh() {
        return sharedPreferences.getLong(KEY_LAST_CLIENT_REFRESH, 0);
    }

    public static class SettingsWriter {
        private final SharedPreferences.Editor editor;

        public SettingsWriter(Context context) {

            editor = context.getSharedPreferences(context.getPackageName() + "_preferences", 0).edit();
        }

        public void setUsername(String username) {
            editor.putString(KEY_USERNAME, username);
        }

        public void setPassword(String password) {
            editor.putString(KEY_PASSWORD, password);
        }

        public void setLastClientRefresh(long lastClientRefresh) {
            editor.putLong(KEY_LAST_CLIENT_REFRESH, lastClientRefresh);
        }

        public void writeEdits() {
            editor.commit();
        }

        public void writeEditsAsync() {
            editor.apply();
        }
    }
}