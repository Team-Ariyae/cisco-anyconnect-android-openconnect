package app.openconnect;

import android.content.SharedPreferences;
import java.util.Locale;
import java.util.UUID;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/VpnProfile.class */
public class VpnProfile implements Comparable<VpnProfile> {
    public static final String INLINE_TAG = "[[INLINE]]";
    public String mName;
    public SharedPreferences mPrefs;
    private UUID mUuid;

    public VpnProfile(SharedPreferences sharedPreferences) {
        loadPrefs(sharedPreferences);
    }

    public VpnProfile(SharedPreferences sharedPreferences, String str, String str2) {
        sharedPreferences.edit().putString("profile_uuid", str).putString("profile_name", str2).commit();
        loadPrefs(sharedPreferences);
    }

    public VpnProfile(String str, String str2) {
        this.mUuid = UUID.fromString(str2);
        this.mName = str;
    }

    private void loadPrefs(SharedPreferences sharedPreferences) {
        this.mPrefs = sharedPreferences;
        String string = sharedPreferences.getString("profile_uuid", null);
        if (string != null) {
            this.mUuid = UUID.fromString(string);
        }
        this.mName = this.mPrefs.getString("profile_name", null);
    }

    @Override // java.lang.Comparable
    public int compareTo(VpnProfile vpnProfile) {
        Locale locale = Locale.getDefault();
        return getName().toUpperCase(locale).compareTo(vpnProfile.getName().toUpperCase(locale));
    }

    public String getName() {
        return this.mName;
    }

    public UUID getUUID() {
        return this.mUuid;
    }

    public String getUUIDString() {
        return this.mUuid.toString();
    }

    public boolean isValid() {
        return (this.mName == null || this.mUuid == null) ? false : true;
    }

    public String toString() {
        return this.mName;
    }
}
