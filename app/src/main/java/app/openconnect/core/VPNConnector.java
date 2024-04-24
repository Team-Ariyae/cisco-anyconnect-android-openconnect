package app.openconnect.core;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import app.openconnect.core.OpenVpnService;
import io.github.inflationx.calligraphy3.BuildConfig;
import org.infradead.libopenconnect.LibOpenConnect;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/VPNConnector.class */
public abstract class VPNConnector {
    public static final String TAG = "OpenConnect";
    private Context mContext;
    private boolean mIsActivity;
    private String mOwnerName;
    private BroadcastReceiver mReceiver;
    private Handler mStatsHandler;
    private Runnable mStatsRunnable;
    public OpenVpnService service;
    public LibOpenConnect.VPNStats oldStats = new LibOpenConnect.VPNStats();
    public LibOpenConnect.VPNStats newStats = new LibOpenConnect.VPNStats();
    public LibOpenConnect.VPNStats deltaStats = new LibOpenConnect.VPNStats();
    public boolean statsValid = false;
    private int mStatsCount = 0;
    private ServiceConnection mConnection = new ServiceConnection(this) { // from class: app.openconnect.core.VPNConnector.3
        public final VPNConnector this$0;

        {
            this.this$0 = this;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            this.this$0.service = ((OpenVpnService.LocalBinder) iBinder).getService();
            VPNConnector vPNConnector = this.this$0;
            vPNConnector.service.updateActivityRefcount(vPNConnector.mIsActivity ? 1 : 0);
            VPNConnector vPNConnector2 = this.this$0;
            vPNConnector2.onUpdate(vPNConnector2.service);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            this.this$0.service = null;
            Log.w("OpenConnect", this.this$0.mOwnerName + " was forcibly unbound from OpenVpnService");
        }
    };

    public VPNConnector(Context context, boolean z7) {
        this.mContext = context;
        this.mIsActivity = z7;
        Intent intent = new Intent(this.mContext, (Class<?>) OpenVpnService.class);
        intent.setAction(OpenVpnService.START_SERVICE);
        this.mContext.bindService(intent, this.mConnection, 1);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver(this) { // from class: app.openconnect.core.VPNConnector.1
            public final VPNConnector this$0;

            {
                this.this$0 = this;
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent2) {
                VPNConnector vPNConnector = this.this$0;
                OpenVpnService openVpnService = vPNConnector.service;
                if (openVpnService != null) {
                    vPNConnector.onUpdate(openVpnService);
                }
            }
        };
        this.mReceiver = broadcastReceiver;
        this.mContext.registerReceiver(broadcastReceiver, new IntentFilter(OpenVpnService.ACTION_VPN_STATUS));
        this.mOwnerName = this.mContext.getClass().getSimpleName();
        this.mStatsHandler = new Handler();
        Runnable runnable = new Runnable(this) { // from class: app.openconnect.core.VPNConnector.2
            public final VPNConnector this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                VPNConnector vPNConnector = this.this$0;
                OpenVpnService openVpnService = vPNConnector.service;
                if (openVpnService != null) {
                    vPNConnector.oldStats = vPNConnector.newStats;
                    vPNConnector.newStats = openVpnService.getStats();
                    VPNConnector vPNConnector2 = this.this$0;
                    LibOpenConnect.VPNStats vPNStats = vPNConnector2.deltaStats;
                    LibOpenConnect.VPNStats vPNStats2 = vPNConnector2.newStats;
                    long j7 = vPNStats2.rxBytes;
                    LibOpenConnect.VPNStats vPNStats3 = vPNConnector2.oldStats;
                    vPNStats.rxBytes = j7 - vPNStats3.rxBytes;
                    vPNStats.rxPkts = vPNStats2.rxPkts - vPNStats3.rxPkts;
                    vPNStats.txBytes = vPNStats2.txBytes - vPNStats3.txBytes;
                    vPNStats.txPkts = vPNStats2.txPkts - vPNStats3.txPkts;
                    vPNConnector2.service.requestStats();
                    if (VPNConnector.access$004(this.this$0) >= 2) {
                        this.this$0.statsValid = true;
                    }
                }
                this.this$0.mStatsHandler.postDelayed(this.this$0.mStatsRunnable, 1000L);
            }
        };
        this.mStatsRunnable = runnable;
        runnable.run();
    }

    public static /* synthetic */ int access$004(VPNConnector vPNConnector) {
        int i7 = vPNConnector.mStatsCount + 1;
        vPNConnector.mStatsCount = i7;
        return i7;
    }

    public String getByteCountSummary() {
        return !this.statsValid ? BuildConfig.FLAVOR : this.mContext.getString(2131755838, OpenVpnService.humanReadableByteCount(this.newStats.rxBytes, false), OpenVpnService.humanReadableByteCount(this.deltaStats.rxBytes, true), OpenVpnService.humanReadableByteCount(this.newStats.txBytes, false), OpenVpnService.humanReadableByteCount(this.deltaStats.txBytes, true));
    }

    public abstract void onUpdate(OpenVpnService openVpnService);

    public void stop() {
        BroadcastReceiver broadcastReceiver = this.mReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
            this.mReceiver = null;
        }
        Handler handler = this.mStatsHandler;
        if (handler != null) {
            handler.removeCallbacks(this.mStatsRunnable);
            this.mStatsHandler = null;
        }
    }

    public void stopActiveDialog() {
        stop();
        OpenVpnService openVpnService = this.service;
        if (openVpnService != null) {
            openVpnService.stopActiveDialog(this.mContext);
        }
    }

    public void unbind() {
        stop();
        OpenVpnService openVpnService = this.service;
        if (openVpnService != null) {
            openVpnService.updateActivityRefcount(this.mIsActivity ? -1 : 0);
        }
        this.mContext.unbindService(this.mConnection);
    }
}
