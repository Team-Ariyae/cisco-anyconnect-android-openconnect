package app.openconnect.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import androidx.activity.result.a;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/DeviceStateReceiver.class */
public class DeviceStateReceiver extends BroadcastReceiver {
    public static final String PREF_CHANGED = "app.openconnect.PREF_CHANGED";
    public static final String TAG = "OpenConnect";
    private boolean mKeepaliveActive;
    private OpenVPNManagement mManagement;
    private boolean mNetchangeReconnect;
    private boolean mNetworkOff;
    private int mNetworkType = -1;
    private boolean mPauseOnScreenOff;
    private boolean mPaused;
    private SharedPreferences mPrefs;
    private boolean mScreenOff;

    public DeviceStateReceiver(OpenVPNManagement openVPNManagement, SharedPreferences sharedPreferences) {
        this.mManagement = openVPNManagement;
        this.mPrefs = sharedPreferences;
        readPrefs();
    }

    private void networkStateChange(Context context) {
        boolean z7;
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo == null || activeNetworkInfo.getState() != NetworkInfo.State.CONNECTED) {
            z7 = true;
        } else {
            int type = activeNetworkInfo.getType();
            int i7 = this.mNetworkType;
            if (i7 != -1 && i7 != type && !this.mPaused && this.mNetchangeReconnect) {
                Log.i("OpenConnect", "reconnecting due to network type change");
                this.mManagement.reconnect();
            }
            this.mNetworkType = type;
            z7 = false;
        }
        this.mNetworkOff = z7;
    }

    private void readPrefs() {
        this.mPauseOnScreenOff = this.mPrefs.getBoolean("screenoff", false);
        this.mNetchangeReconnect = this.mPrefs.getBoolean("netchangereconnect", true);
    }

    private void updatePauseState() {
        boolean z7 = this.mPauseOnScreenOff && this.mScreenOff && !this.mKeepaliveActive;
        if (this.mNetworkOff) {
            z7 = true;
        }
        if (z7 && !this.mPaused) {
            StringBuilder r7 = a.r("pausing: mScreenOff=");
            r7.append(this.mScreenOff);
            r7.append(" mNetworkOff=");
            r7.append(this.mNetworkOff);
            Log.i("OpenConnect", r7.toString());
            this.mManagement.pause();
        } else if (!z7 && this.mPaused) {
            StringBuilder r8 = a.r("resuming: mScreenOff=");
            r8.append(this.mScreenOff);
            r8.append(" mNetworkOff=");
            r8.append(this.mNetworkOff);
            Log.i("OpenConnect", r8.toString());
            this.mManagement.resume();
        }
        this.mPaused = z7;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        boolean z7;
        String action = intent.getAction();
        if (PREF_CHANGED.equals(action)) {
            this.mManagement.prefChanged();
            readPrefs();
        } else if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            if (!"android.intent.action.SCREEN_OFF".equals(action)) {
                if ("android.intent.action.SCREEN_ON".equals(action)) {
                    z7 = false;
                }
                updatePauseState();
            }
            z7 = true;
            this.mScreenOff = z7;
            updatePauseState();
        }
        networkStateChange(context);
        updatePauseState();
    }

    public void setKeepalive(boolean z7) {
        this.mKeepaliveActive = z7;
        updatePauseState();
    }
}
