package app.openconnect.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.activity.result.a;
import app.openconnect.VpnProfile;
import io.github.inflationx.calligraphy3.BuildConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import org.strongswan.android.data.VpnProfileDataSource;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/ProfileManager.class */
public class ProfileManager {
    private static final String ON_BOOT_PROFILE = "onBootProfile";
    private static final String PROFILE_PFX = "profile-";
    private static final String RESTART_ON_BOOT = "restartvpnonboot_FIXME";
    public static final String TAG = "OpenConnect";
    private static SharedPreferences mAppPrefs;
    private static Context mContext;
    private static HashMap<String, VpnProfile> mProfiles;
    public static String[] fileSelectKeys = {"ca_certificate", VpnProfileDataSource.KEY_USER_CERTIFICATE, "private_key", "custom_csd_wrapper"};
    private static VpnProfile mLastConnectedVpn = null;

    private static String capitalize(String str) {
        if (str.length() <= 4) {
            return str.toUpperCase(Locale.getDefault());
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static VpnProfile create(String str) {
        VpnProfile vpnProfile;
        synchronized (ProfileManager.class) {
            int i7 = 0;
            while (true) {
                try {
                    String makeProfName = makeProfName(str, i7);
                    if (getProfileByName(makeProfName) == null) {
                        String uuid = UUID.randomUUID().toString();
                        SharedPreferences sharedPreferences = mContext.getSharedPreferences(getPrefsName(uuid), 0);
                        sharedPreferences.edit().putString("server_address", str).commit();
                        vpnProfile = new VpnProfile(sharedPreferences, uuid, makeProfName);
                        mProfiles.put(uuid, vpnProfile);
                    } else {
                        i7++;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return vpnProfile;
    }

    public static boolean delete(String str) {
        synchronized (ProfileManager.class) {
            try {
                VpnProfile vpnProfile = get(str);
                if (vpnProfile == null) {
                    Log.w("OpenConnect", "error looking up profile " + str);
                    return false;
                }
                for (String str2 : fileSelectKeys) {
                    deleteFilePref(vpnProfile, str2);
                }
                mProfiles.remove(str);
                StringBuilder sb = new StringBuilder();
                sb.append(mContext.getApplicationInfo().dataDir);
                String str3 = File.separator;
                sb.append(str3);
                sb.append("shared_prefs");
                sb.append(str3);
                sb.append(PROFILE_PFX);
                sb.append(str);
                sb.append(".xml");
                if (new File(sb.toString()).delete()) {
                    Log.i("OpenConnect", "deleted profile " + str);
                    return true;
                }
                Log.w("OpenConnect", "error deleting profile " + str);
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void deleteFilePref(VpnProfile vpnProfile, String str) {
        synchronized (ProfileManager.class) {
            try {
                String string = vpnProfile.mPrefs.getString(str, null);
                if (getCertFilename(vpnProfile, str).equals(string)) {
                    if (!new File(getCertPath() + string).delete()) {
                        Log.w("OpenConnect", "error deleting " + string);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static VpnProfile get(String str) {
        VpnProfile vpnProfile;
        synchronized (ProfileManager.class) {
            if (str == null) {
                vpnProfile = null;
            } else {
                try {
                    vpnProfile = mProfiles.get(str);
                } finally {
                }
            }
        }
        return vpnProfile;
    }

    private static String getCertFilename(VpnProfile vpnProfile, String str) {
        StringBuilder r7 = a.r("cert.");
        r7.append(vpnProfile.getUUIDString());
        r7.append(".");
        r7.append(str);
        return r7.toString();
    }

    public static String getCertPath() {
        return mContext.getFilesDir().getPath() + File.separator;
    }

    public static VpnProfile getLastConnectedVpn() {
        return mLastConnectedVpn;
    }

    public static VpnProfile getOnBootProfile() {
        synchronized (ProfileManager.class) {
            try {
                if (!mAppPrefs.getBoolean(RESTART_ON_BOOT, false)) {
                    return null;
                }
                return get(mAppPrefs.getString(ON_BOOT_PROFILE, null));
            } finally {
            }
        }
    }

    public static String getPrefsName(String str) {
        return a.m(PROFILE_PFX, str);
    }

    public static VpnProfile getProfileByName(String str) {
        VpnProfile next;
        synchronized (ProfileManager.class) {
            try {
                String lowerCase = str.toLowerCase(Locale.getDefault());
                Iterator<VpnProfile> it = mProfiles.values().iterator();
                do {
                    if (!it.hasNext()) {
                        return null;
                    }
                    next = it.next();
                } while (!next.getName().toLowerCase(Locale.getDefault()).equals(lowerCase));
                return next;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static Collection<VpnProfile> getProfiles() {
        Collection<VpnProfile> values;
        synchronized (ProfileManager.class) {
            try {
                init(mContext);
                values = mProfiles.values();
            } catch (Throwable th) {
                throw th;
            }
        }
        return values;
    }

    public static void init(Context context) {
        mContext = context;
        mAppPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mProfiles = new HashMap<>();
        File file = new File(context.getApplicationInfo().dataDir, "shared_prefs");
        if (file.exists() && file.isDirectory()) {
            for (String str : file.list()) {
                if (str.startsWith(PROFILE_PFX)) {
                    VpnProfile vpnProfile = new VpnProfile(context.getSharedPreferences(str.replaceFirst(".xml", BuildConfig.FLAVOR), 0));
                    if (vpnProfile.isValid()) {
                        mProfiles.put(vpnProfile.getUUIDString(), vpnProfile);
                    } else {
                        Log.w("OpenConnect", "removing bogus profile '" + str + "'");
                        new File(str).delete();
                    }
                }
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x009d, code lost:
    
        if (r0.trim().equals(io.github.inflationx.calligraphy3.BuildConfig.FLAVOR) != false) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x010c, code lost:
    
        if (r0.equals("com") != false) goto L42;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static java.lang.String makeProfName(java.lang.String r4, int r5) {
        /*
            Method dump skipped, instructions count: 316
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: app.openconnect.core.ProfileManager.makeProfName(java.lang.String, int):java.lang.String");
    }

    public static void setConnectedVpnProfile(VpnProfile vpnProfile) {
        synchronized (ProfileManager.class) {
            try {
                mLastConnectedVpn = vpnProfile;
                mAppPrefs.edit().putString(ON_BOOT_PROFILE, vpnProfile.getUUIDString()).commit();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void setConnectedVpnProfileDisconnected() {
        synchronized (ProfileManager.class) {
            try {
                mLastConnectedVpn = null;
                mAppPrefs.edit().remove(ON_BOOT_PROFILE).commit();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static String storeFilePref(VpnProfile vpnProfile, String str, String str2) {
        String certFilename;
        synchronized (ProfileManager.class) {
            try {
                certFilename = getCertFilename(vpnProfile, str);
                String str3 = getCertPath() + certFilename;
                try {
                    FileInputStream fileInputStream = new FileInputStream(str2);
                    File file = new File(str3);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte[] bArr = new byte[65536];
                    fileOutputStream.write(bArr, 0, fileInputStream.read(bArr));
                    fileInputStream.close();
                    fileOutputStream.close();
                    file.setExecutable(true);
                } catch (Exception e8) {
                    Log.e("OpenConnect", "error copying " + str2 + " -> " + str3, e8);
                    try {
                        new File(str3).delete();
                        return null;
                    } catch (Exception e9) {
                        return null;
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return certFilename;
    }
}
