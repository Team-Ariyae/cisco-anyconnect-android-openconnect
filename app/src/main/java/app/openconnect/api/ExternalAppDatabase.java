package app.openconnect.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.HashSet;
import java.util.Set;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/api/ExternalAppDatabase.class */
public class ExternalAppDatabase {
    private final String PREFERENCES_KEY = "PREFERENCES_KEY";
    public Context mContext;

    public ExternalAppDatabase(Context context) {
        this.mContext = context;
    }

    private void saveExtAppList(Set<String> set) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edit.putStringSet("PREFERENCES_KEY", set);
        edit.apply();
    }

    public void addApp(String str) {
        Set<String> extAppList = getExtAppList();
        extAppList.add(str);
        saveExtAppList(extAppList);
    }

    public void clearAllApiApps() {
        saveExtAppList(new HashSet());
    }

    public Set<String> getExtAppList() {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext).getStringSet("PREFERENCES_KEY", new HashSet());
    }

    public boolean isAllowed(String str) {
        return getExtAppList().contains(str);
    }

    public void removeApp(String str) {
        Set<String> extAppList = getExtAppList();
        extAppList.remove(str);
        saveExtAppList(extAppList);
    }
}
