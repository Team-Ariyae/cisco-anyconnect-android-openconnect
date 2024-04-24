package app.openconnect.core;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import app.openconnect.VpnProfile;
import app.openconnect.api.GrantPermissionsActivity;
import app.openconnect.core.VPNLog;
import io.github.inflationx.calligraphy3.BuildConfig;
import java.net.InetAddress;
import java.util.Date;
import java.util.Locale;
import org.infradead.libopenconnect.LibOpenConnect;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/OpenVpnService.class */
public class OpenVpnService extends VpnService {
    public static final String ACTION_VPN_STATUS = "app.openconnect.VPN_STATUS";
    public static final String ALWAYS_SHOW_NOTIFICATION = "app.openconnect.NOTIFICATION_ALWAYS_VISIBLE";
    public static final String EXTRA_CONNECTION_STATE = "app.openconnect.connectionState";
    public static final String EXTRA_UUID = "app.openconnect.UUID";
    public static final String START_SERVICE = "app.openconnect.START_SERVICE";
    public static final String START_SERVICE_STICKY = "app.openconnect.START_SERVICE_STICKY";
    public static final String TAG = "OpenConnect";
    public LibOpenConnect.IPInfo ipInfo;
    private int mActivityConnections;
    private String[] mConnectionStateNames;
    private DeviceStateReceiver mDeviceStateReceiver;
    private UserDialog mDialog;
    private Context mDialogContext;
    private KeepAlive mKeepAlive;
    private boolean mNotificationActive;
    private SharedPreferences mPrefs;
    private int mStartId;
    private String mUUID;
    private OpenConnectManagementThread mVPN;
    private Thread mVPNThread;
    public VpnProfile profile;
    public String serverName;
    public Date startTime;
    private final IBinder mBinder = new LocalBinder(this);
    private final int NOTIFICATION_ID = 1;
    private int mConnectionState = 6;
    private LibOpenConnect.VPNStats mStats = new LibOpenConnect.VPNStats();
    private VPNLog mVPNLog = new VPNLog();
    private Handler mHandler = new Handler();

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/core/OpenVpnService$LocalBinder.class */
    public class LocalBinder extends Binder {
        public final OpenVpnService this$0;

        public LocalBinder(OpenVpnService openVpnService) {
            this.this$0 = openVpnService;
        }

        public OpenVpnService getService() {
            return this.this$0;
        }
    }

    private boolean doStopVPN() {
        boolean z7;
        synchronized (this) {
            OpenConnectManagementThread openConnectManagementThread = this.mVPN;
            if (openConnectManagementThread != null) {
                openConnectManagementThread.stopVPN();
                z7 = true;
            } else {
                z7 = false;
            }
        }
        return z7;
    }

    public static String formatElapsedTime(long j7) {
        StringBuilder sb = new StringBuilder();
        long time = (new Date().getTime() - j7) / 1000;
        if (time >= 86400) {
            sb.append(String.format("%1$d:", Long.valueOf(time / 86400)));
        }
        long j8 = time;
        if (time >= 3600) {
            long j9 = time % 86400;
            sb.append(String.format("%1$02d:", Long.valueOf(j9 / 3600)));
            j8 = j9 % 3600;
        }
        sb.append(String.format("%1$02d:%2$02d", Long.valueOf(j8 / 60), Long.valueOf(j8 % 60)));
        return sb.toString();
    }

    private PendingIntent getMainActivityIntent() {
        return null;
    }

    public static String humanReadableByteCount(long j7, boolean z7) {
        long j8 = j7;
        if (z7) {
            j8 = j7 * 8;
        }
        int i7 = z7 ? 1000 : 1024;
        if (j8 < i7) {
            StringBuilder sb = new StringBuilder();
            sb.append(j8);
            sb.append(z7 ? " bit" : " B");
            return sb.toString();
        }
        double d8 = j8;
        double log = Math.log(d8);
        double d9 = i7;
        int log2 = (int) (log / Math.log(d9));
        StringBuilder sb2 = new StringBuilder();
        sb2.append((z7 ? "kMGTPE" : "KMGTPE").charAt(log2 - 1));
        sb2.append(BuildConfig.FLAVOR);
        String sb3 = sb2.toString();
        return z7 ? String.format(Locale.getDefault(), "%.1f %sbit", Double.valueOf(d8 / Math.pow(d9, log2)), sb3) : String.format(Locale.getDefault(), "%.1f %sB", Double.valueOf(d8 / Math.pow(d9, log2)), sb3);
    }

    private void killVPNThread(boolean z7) {
        if (doStopVPN() && z7) {
            try {
                this.mVPNThread.join(1000L);
            } catch (InterruptedException e8) {
                Log.e("OpenConnect", "OpenConnect thread did not exit");
            }
        }
    }

    private void registerDeviceStateReceiver(OpenVPNManagement openVPNManagement) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction(DeviceStateReceiver.PREF_CHANGED);
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        DeviceStateReceiver deviceStateReceiver = new DeviceStateReceiver(openVPNManagement, this.mPrefs);
        this.mDeviceStateReceiver = deviceStateReceiver;
        registerReceiver(deviceStateReceiver, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerKeepAlive() {
        int i7;
        synchronized (this) {
            String str = "8.8.8.8";
            try {
                String str2 = this.ipInfo.DNS.get(0);
                if (InetAddress.getByName(str2) != null) {
                    str = str2;
                }
            } catch (IndexOutOfBoundsException e8) {
            } catch (Exception e9) {
                Log.i("OpenConnect", "server DNS IP is bogus, falling back to 8.8.8.8 for KeepAlive", e9);
            }
            try {
                int parseInt = Integer.parseInt(this.ipInfo.CSTPOptions.get("X-CSTP-Idle-Timeout"));
                i7 = 1800;
                if (parseInt >= 60) {
                    i7 = 1800;
                    if (parseInt <= 7200) {
                        i7 = parseInt;
                    }
                }
            } catch (Exception e10) {
                i7 = 1800;
            }
            int i8 = (i7 * 4) / 10;
            Log.d("OpenConnect", "calculated KeepAlive interval: " + i8 + " seconds");
            IntentFilter intentFilter = new IntentFilter(KeepAlive.ACTION_KEEPALIVE_ALARM);
            KeepAlive keepAlive = new KeepAlive(i8, str, this.mDeviceStateReceiver);
            this.mKeepAlive = keepAlive;
            registerReceiver(keepAlive, intentFilter);
            this.mKeepAlive.start(this);
        }
    }

    private void setDialog(Context context, UserDialog userDialog) {
        synchronized (this) {
            this.mDialogContext = context;
            this.mDialog = userDialog;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterReceivers() {
        try {
            DeviceStateReceiver deviceStateReceiver = this.mDeviceStateReceiver;
            if (deviceStateReceiver != null) {
                unregisterReceiver(deviceStateReceiver);
            }
            this.mDeviceStateReceiver = null;
        } catch (IllegalArgumentException e8) {
            Log.w("OpenConnect", "can't unregister DeviceStateReceiver", e8);
        }
        try {
            KeepAlive keepAlive = this.mKeepAlive;
            if (keepAlive != null) {
                keepAlive.stop(this);
                unregisterReceiver(this.mKeepAlive);
            }
            this.mKeepAlive = null;
        } catch (IllegalArgumentException e9) {
            Log.w("OpenConnect", "can't unregister KeepAlive", e9);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotification() {
    }

    private void wakeUpActivity() {
        this.mHandler.post(new Runnable(this) { // from class: app.openconnect.core.OpenVpnService.1
            public final OpenVpnService this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                Intent intent = new Intent(OpenVpnService.ACTION_VPN_STATUS);
                intent.putExtra(OpenVpnService.EXTRA_CONNECTION_STATE, this.this$0.mConnectionState);
                intent.putExtra(OpenVpnService.EXTRA_UUID, this.this$0.mUUID);
                this.this$0.sendBroadcast(intent, "android.permission.ACCESS_NETWORK_STATE");
                this.this$0.updateNotification();
                if (this.this$0.mConnectionState == 5 && this.this$0.mKeepAlive == null) {
                    this.this$0.registerKeepAlive();
                }
            }
        });
    }

    public void clearLog() {
        this.mVPNLog.clear();
    }

    public String dumpLog() {
        return this.mVPNLog.dump();
    }

    public VPNLog.LogArrayAdapter getArrayAdapter(Context context) {
        return this.mVPNLog.getArrayAdapter(context);
    }

    public int getConnectionState() {
        int i7;
        synchronized (this) {
            i7 = this.mConnectionState;
        }
        return i7;
    }

    public String getConnectionStateName() {
        return this.mConnectionStateNames[getConnectionState()];
    }

    public String getReconnectName() {
        VpnProfile vpnProfile = ProfileManager.get(this.mUUID);
        return vpnProfile == null ? null : vpnProfile.getName();
    }

    public LibOpenConnect.VPNStats getStats() {
        LibOpenConnect.VPNStats vPNStats;
        synchronized (this) {
            vPNStats = this.mStats;
        }
        return vPNStats;
    }

    public VpnService.Builder getVpnServiceBuilder() {
        VpnService.Builder builder = new VpnService.Builder(this);
        builder.setSession("TehVPN VPN");
        return builder;
    }

    public void log(int i7, String str) {
        this.mHandler.post(new Runnable(this, i7, str) { // from class: app.openconnect.core.OpenVpnService.3
            public final OpenVpnService this$0;
            public final int val$level;
            public final String val$msg;

            {
                this.this$0 = this;
                this.val$level = i7;
                this.val$msg = str;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.mVPNLog.add(this.val$level, this.val$msg);
            }
        });
    }

    @Override // android.net.VpnService, android.app.Service
    public IBinder onBind(Intent intent) {
        String action = intent.getAction();
        return (action == null || !action.equals(START_SERVICE)) ? super.onBind(intent) : this.mBinder;
    }

    @Override // android.app.Service
    public void onCreate() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.mPrefs = defaultSharedPreferences;
        this.mUUID = defaultSharedPreferences.getString("service_mUUID", BuildConfig.FLAVOR);
        this.mVPNLog.restoreFromFile(getCacheDir().getAbsolutePath() + "/logdata.ser");
        this.mConnectionStateNames = getResources().getStringArray(2130903043);
    }

    @Override // android.app.Service
    public void onDestroy() {
        killVPNThread(true);
        DeviceStateReceiver deviceStateReceiver = this.mDeviceStateReceiver;
        if (deviceStateReceiver != null) {
            unregisterReceiver(deviceStateReceiver);
        }
        this.mVPNLog.saveToFile(getCacheDir().getAbsolutePath() + "/logdata.ser");
    }

    @Override // android.net.VpnService
    public void onRevoke() {
        Log.i("OpenConnect", "VPN access has been revoked");
        stopVPN();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i7, int i8) {
        if (intent == null) {
            Log.e("OpenConnect", "OpenVpnService started with null intent");
            stopSelf();
            return 2;
        }
        String action = intent.getAction();
        if (START_SERVICE.equals(action)) {
            return 2;
        }
        if (START_SERVICE_STICKY.equals(action)) {
            return 3;
        }
        String stringExtra = intent.getStringExtra(EXTRA_UUID);
        this.mUUID = stringExtra;
        if (stringExtra == null) {
            return 2;
        }
        this.mPrefs.edit().putString("service_mUUID", this.mUUID).apply();
        VpnProfile vpnProfile = ProfileManager.get(this.mUUID);
        this.profile = vpnProfile;
        if (vpnProfile == null) {
            return 2;
        }
        killVPNThread(true);
        this.mStartId = i8;
        this.mVPN = new OpenConnectManagementThread(getApplicationContext(), this.profile, this);
        Thread thread = new Thread(this.mVPN, "OpenVPNManagementThread");
        this.mVPNThread = thread;
        thread.start();
        unregisterReceivers();
        registerDeviceStateReceiver(this.mVPN);
        ProfileManager.setConnectedVpnProfile(this.profile);
        return 2;
    }

    public Object promptUser(UserDialog userDialog) {
        Object earlyReturn = userDialog.earlyReturn();
        if (earlyReturn != null) {
            return earlyReturn;
        }
        setDialog(null, userDialog);
        wakeUpActivity();
        Object waitForResponse = this.mDialog.waitForResponse();
        setDialog(null, null);
        return waitForResponse;
    }

    public void putArrayAdapter(VPNLog.LogArrayAdapter logArrayAdapter) {
        if (logArrayAdapter != null) {
            this.mVPNLog.putArrayAdapter(logArrayAdapter);
        }
    }

    public void requestStats() {
        OpenConnectManagementThread openConnectManagementThread = this.mVPN;
        if (openConnectManagementThread != null) {
            openConnectManagementThread.requestStats();
        }
    }

    public void setConnectionState(int i7) {
        synchronized (this) {
            if (i7 == 5) {
                if (this.mConnectionState != 5) {
                    this.startTime = new Date();
                }
            }
            this.mConnectionState = i7;
            wakeUpActivity();
        }
    }

    public void setIPInfo(LibOpenConnect.IPInfo iPInfo, String str) {
        synchronized (this) {
            this.ipInfo = iPInfo;
            this.serverName = str;
        }
    }

    public void setStats(LibOpenConnect.VPNStats vPNStats) {
        synchronized (this) {
            if (vPNStats != null) {
                this.mStats = vPNStats;
            }
            wakeUpActivity();
        }
    }

    public void startActiveDialog(Context context) {
        synchronized (this) {
            UserDialog userDialog = this.mDialog;
            if (userDialog != null && this.mDialogContext == null) {
                this.mDialogContext = context;
                userDialog.onStart(context);
            }
        }
    }

    public void startReconnectActivity(Context context) {
        Intent intent = new Intent(context, (Class<?>) GrantPermissionsActivity.class);
        intent.putExtra(getPackageName() + GrantPermissionsActivity.EXTRA_UUID, this.mUUID);
        context.startActivity(intent);
    }

    public void stopActiveDialog(Context context) {
        synchronized (this) {
            Context context2 = this.mDialogContext;
            if (context2 != context) {
                return;
            }
            UserDialog userDialog = this.mDialog;
            if (userDialog != null) {
                userDialog.onStop(context2);
            }
            this.mDialogContext = null;
        }
    }

    public void stopVPN() {
        killVPNThread(false);
        ProfileManager.setConnectedVpnProfileDisconnected();
    }

    public void stopVPN(boolean z7) {
        killVPNThread(z7);
        ProfileManager.setConnectedVpnProfileDisconnected();
    }

    public void threadDone() {
        synchronized (this) {
            int i7 = this.mStartId;
            Log.i("OpenConnect", "VPN thread has terminated");
            this.mVPN = null;
            this.mHandler.post(new Runnable(this, i7) { // from class: app.openconnect.core.OpenVpnService.2
                public final OpenVpnService this$0;
                public final int val$startId;

                {
                    this.this$0 = this;
                    this.val$startId = i7;
                }

                @Override // java.lang.Runnable
                public void run() {
                    if (this.this$0.stopSelfResult(this.val$startId)) {
                        this.this$0.unregisterReceivers();
                    } else {
                        Log.w("OpenConnect", "not stopping service due to startId mismatch");
                    }
                }
            });
        }
    }

    public void updateActivityRefcount(int i7) {
        this.mActivityConnections += i7;
        updateNotification();
    }
}
