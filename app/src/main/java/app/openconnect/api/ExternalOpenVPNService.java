package app.openconnect.api;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.VpnService;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import app.openconnect.VpnProfile;
import app.openconnect.api.IOpenVPNAPIService;
import app.openconnect.core.OpenVPN;
import app.openconnect.core.OpenVpnService;
import app.openconnect.core.ProfileManager;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/api/ExternalOpenVPNService.class */
public class ExternalOpenVPNService extends Service {
    private static final int SEND_TOALL = 0;
    private static final OpenVPNServiceHandler mHandler = new OpenVPNServiceHandler();
    private ExternalAppDatabase mExtAppDb;
    private UpdateMessage mMostRecentState;
    private OpenVpnService mService;
    public final RemoteCallbackList<IOpenVPNStatusCallback> mCallbacks = new RemoteCallbackList<>();
    private ServiceConnection mConnection = new ServiceConnection(this) { // from class: app.openconnect.api.ExternalOpenVPNService.1
        public final ExternalOpenVPNService this$0;

        {
            this.this$0 = this;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            this.this$0.mService = ((OpenVpnService.LocalBinder) iBinder).getService();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            this.this$0.mService = null;
        }
    };
    private final IOpenVPNAPIService.Stub mBinder = new IOpenVPNAPIService.Stub(this) { // from class: app.openconnect.api.ExternalOpenVPNService.2
        public final ExternalOpenVPNService this$0;

        {
            this.this$0 = this;
        }

        private void checkOpenVPNPermission() {
            PackageManager packageManager = this.this$0.getPackageManager();
            for (String str : this.this$0.mExtAppDb.getExtAppList()) {
                try {
                } catch (PackageManager.NameNotFoundException e8) {
                    this.this$0.mExtAppDb.removeApp(str);
                    e8.printStackTrace();
                }
                if (Binder.getCallingUid() == packageManager.getApplicationInfo(str, 0).uid) {
                    return;
                }
            }
            throw new SecurityException("Unauthorized OpenVPN API Caller");
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public boolean addVPNProfile(String str, String str2) {
            checkOpenVPNPermission();
            return true;
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public void disconnect() {
            checkOpenVPNPermission();
            if (this.this$0.mService != null) {
                this.this$0.mService.stopVPN();
            }
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public List<APIVpnProfile> getProfiles() {
            checkOpenVPNPermission();
            LinkedList linkedList = new LinkedList();
            for (VpnProfile vpnProfile : ProfileManager.getProfiles()) {
                linkedList.add(new APIVpnProfile(vpnProfile.getUUIDString(), vpnProfile.mName, true));
            }
            return linkedList;
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public Intent prepare(String str) {
            if (new ExternalAppDatabase(this.this$0).isAllowed(str)) {
                return null;
            }
            Intent intent = new Intent();
            intent.setClass(this.this$0, ConfirmDialog.class);
            return intent;
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public Intent prepareVPNService() {
            checkOpenVPNPermission();
            if (VpnService.prepare(this.this$0) == null) {
                return null;
            }
            return new Intent(this.this$0.getBaseContext(), (Class<?>) GrantPermissionsActivity.class);
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public void registerStatusCallback(IOpenVPNStatusCallback iOpenVPNStatusCallback) {
            checkOpenVPNPermission();
            if (iOpenVPNStatusCallback != null) {
                iOpenVPNStatusCallback.newStatus(this.this$0.mMostRecentState.vpnUUID, this.this$0.mMostRecentState.state, this.this$0.mMostRecentState.logmessage, this.this$0.mMostRecentState.level.name());
                this.this$0.mCallbacks.register(iOpenVPNStatusCallback);
            }
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public void startProfile(String str) {
            checkOpenVPNPermission();
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public void startVPN(String str) {
            checkOpenVPNPermission();
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public void unregisterStatusCallback(IOpenVPNStatusCallback iOpenVPNStatusCallback) {
            checkOpenVPNPermission();
            if (iOpenVPNStatusCallback != null) {
                this.this$0.mCallbacks.unregister(iOpenVPNStatusCallback);
            }
        }
    };

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/api/ExternalOpenVPNService$OpenVPNServiceHandler.class */
    public static class OpenVPNServiceHandler extends Handler {
        public WeakReference<ExternalOpenVPNService> service = null;

        private void sendUpdate(IOpenVPNStatusCallback iOpenVPNStatusCallback, UpdateMessage updateMessage) {
            iOpenVPNStatusCallback.newStatus(updateMessage.vpnUUID, updateMessage.state, updateMessage.logmessage, updateMessage.level.name());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setService(ExternalOpenVPNService externalOpenVPNService) {
            this.service = new WeakReference<>(externalOpenVPNService);
        }

        /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxRuntimeException: Can't find top splitter block for handler:B:16:0x005c
            	at jadx.core.utils.BlockUtils.getTopSplitterForHandler(BlockUtils.java:1166)
            	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:1022)
            	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:55)
            */
        @Override // android.os.Handler
        public void handleMessage(android.os.Message r5) {
            /*
                r4 = this;
                r0 = r5
                int r0 = r0.what
                if (r0 == 0) goto La
                goto L5b
            La:
                r0 = r4
                java.lang.ref.WeakReference<app.openconnect.api.ExternalOpenVPNService> r0 = r0.service
                r8 = r0
                r0 = r8
                if (r0 == 0) goto L5b
                r0 = r8
                java.lang.Object r0 = r0.get()
                if (r0 != 0) goto L20
                goto L5b
            L20:
                r0 = r4
                java.lang.ref.WeakReference<app.openconnect.api.ExternalOpenVPNService> r0 = r0.service
                java.lang.Object r0 = r0.get()
                app.openconnect.api.ExternalOpenVPNService r0 = (app.openconnect.api.ExternalOpenVPNService) r0
                android.os.RemoteCallbackList<app.openconnect.api.IOpenVPNStatusCallback> r0 = r0.mCallbacks
                r8 = r0
                r0 = r8
                int r0 = r0.beginBroadcast()
                r7 = r0
                r0 = 0
                r6 = r0
            L37:
                r0 = r6
                r1 = r7
                if (r0 >= r1) goto L56
                r0 = r4
                r1 = r8
                r2 = r6
                android.os.IInterface r1 = r1.getBroadcastItem(r2)     // Catch: android.os.RemoteException -> L5c
                app.openconnect.api.IOpenVPNStatusCallback r1 = (app.openconnect.api.IOpenVPNStatusCallback) r1     // Catch: android.os.RemoteException -> L5c
                r2 = r5
                java.lang.Object r2 = r2.obj     // Catch: android.os.RemoteException -> L5c
                app.openconnect.api.ExternalOpenVPNService$UpdateMessage r2 = (app.openconnect.api.ExternalOpenVPNService.UpdateMessage) r2     // Catch: android.os.RemoteException -> L5c
                r0.sendUpdate(r1, r2)     // Catch: android.os.RemoteException -> L5c
            L50:
                int r6 = r6 + 1
                goto L37
            L56:
                r0 = r8
                r0.finishBroadcast()
            L5b:
                return
            L5c:
                r9 = move-exception
                goto L50
            */
            throw new UnsupportedOperationException("Method not decompiled: app.openconnect.api.ExternalOpenVPNService.OpenVPNServiceHandler.handleMessage(android.os.Message):void");
        }
    }

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/api/ExternalOpenVPNService$UpdateMessage.class */
    public class UpdateMessage {
        public OpenVPN.ConnectionStatus level;
        public String logmessage;
        public String state;
        public final ExternalOpenVPNService this$0;
        public String vpnUUID;

        public UpdateMessage(ExternalOpenVPNService externalOpenVPNService, String str, String str2, OpenVPN.ConnectionStatus connectionStatus) {
            this.this$0 = externalOpenVPNService;
            this.state = str;
            this.logmessage = str2;
            this.level = connectionStatus;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mExtAppDb = new ExternalAppDatabase(this);
        Intent intent = new Intent(getBaseContext(), (Class<?>) OpenVpnService.class);
        intent.setAction(OpenVpnService.START_SERVICE);
        bindService(intent, this.mConnection, 1);
        mHandler.setService(this);
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        this.mCallbacks.kill();
        unbindService(this.mConnection);
    }
}
