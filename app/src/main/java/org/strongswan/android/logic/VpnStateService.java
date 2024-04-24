package org.strongswan.android.logic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.tehvpn.Activities.MainActivity;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.strongswan.android.data.VpnProfile;
import org.strongswan.android.data.VpnProfileDataSource;
import org.strongswan.android.data.VpnType;
import org.strongswan.android.logic.imc.ImcState;
import org.strongswan.android.logic.imc.RemediationInstruction;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/VpnStateService.class */
public class VpnStateService extends Service {
    private static long MAX_RETRY_INTERVAL = 3000;
    private static long RETRY_INTERVAL = 1000;
    private static int RETRY_MSG = 1;
    private Handler mHandler;
    private VpnProfile mProfile;
    private long mRetryIn;
    private long mRetryTimeout;
    private final HashSet<VpnStateListener> mListeners = new HashSet<>();
    private final IBinder mBinder = new LocalBinder(this);
    private long mConnectionID = 0;
    private State mState = State.DISABLED;
    private ErrorState mError = ErrorState.NO_ERROR;
    private ImcState mImcState = ImcState.UNKNOWN;
    private final LinkedList<RemediationInstruction> mRemediationInstructions = new LinkedList<>();
    private RetryTimeoutProvider mTimeoutProvider = new RetryTimeoutProvider();

    /* renamed from: org.strongswan.android.logic.VpnStateService$7, reason: invalid class name */
    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/VpnStateService$7.class */
    public static /* synthetic */ class AnonymousClass7 {
        public static final int[] $SwitchMap$org$strongswan$android$logic$VpnStateService$ErrorState;

        /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxRuntimeException: Can't find top splitter block for handler:B:36:0x005d
            	at jadx.core.utils.BlockUtils.getTopSplitterForHandler(BlockUtils.java:1166)
            	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:1022)
            	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:55)
            */
        static {
            /*
                org.strongswan.android.logic.VpnStateService$ErrorState[] r0 = org.strongswan.android.logic.VpnStateService.ErrorState.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                r4 = r0
                r0 = r4
                org.strongswan.android.logic.VpnStateService.AnonymousClass7.$SwitchMap$org$strongswan$android$logic$VpnStateService$ErrorState = r0
                r0 = r4
                org.strongswan.android.logic.VpnStateService$ErrorState r1 = org.strongswan.android.logic.VpnStateService.ErrorState.AUTH_FAILED     // Catch: java.lang.NoSuchFieldError -> L4d
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L4d
                r2 = 1
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L4d
            L14:
                int[] r0 = org.strongswan.android.logic.VpnStateService.AnonymousClass7.$SwitchMap$org$strongswan$android$logic$VpnStateService$ErrorState     // Catch: java.lang.NoSuchFieldError -> L4d java.lang.NoSuchFieldError -> L51
                org.strongswan.android.logic.VpnStateService$ErrorState r1 = org.strongswan.android.logic.VpnStateService.ErrorState.PEER_AUTH_FAILED     // Catch: java.lang.NoSuchFieldError -> L51
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L51
                r2 = 2
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L51
            L1f:
                int[] r0 = org.strongswan.android.logic.VpnStateService.AnonymousClass7.$SwitchMap$org$strongswan$android$logic$VpnStateService$ErrorState     // Catch: java.lang.NoSuchFieldError -> L51 java.lang.NoSuchFieldError -> L55
                org.strongswan.android.logic.VpnStateService$ErrorState r1 = org.strongswan.android.logic.VpnStateService.ErrorState.LOOKUP_FAILED     // Catch: java.lang.NoSuchFieldError -> L55
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L55
                r2 = 3
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L55
            L2a:
                int[] r0 = org.strongswan.android.logic.VpnStateService.AnonymousClass7.$SwitchMap$org$strongswan$android$logic$VpnStateService$ErrorState     // Catch: java.lang.NoSuchFieldError -> L55 java.lang.NoSuchFieldError -> L59
                org.strongswan.android.logic.VpnStateService$ErrorState r1 = org.strongswan.android.logic.VpnStateService.ErrorState.UNREACHABLE     // Catch: java.lang.NoSuchFieldError -> L59
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L59
                r2 = 4
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L59
            L35:
                int[] r0 = org.strongswan.android.logic.VpnStateService.AnonymousClass7.$SwitchMap$org$strongswan$android$logic$VpnStateService$ErrorState     // Catch: java.lang.NoSuchFieldError -> L59 java.lang.NoSuchFieldError -> L5d
                org.strongswan.android.logic.VpnStateService$ErrorState r1 = org.strongswan.android.logic.VpnStateService.ErrorState.PASSWORD_MISSING     // Catch: java.lang.NoSuchFieldError -> L5d
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L5d
                r2 = 5
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L5d
            L40:
                int[] r0 = org.strongswan.android.logic.VpnStateService.AnonymousClass7.$SwitchMap$org$strongswan$android$logic$VpnStateService$ErrorState     // Catch: java.lang.NoSuchFieldError -> L5d java.lang.NoSuchFieldError -> L61
                org.strongswan.android.logic.VpnStateService$ErrorState r1 = org.strongswan.android.logic.VpnStateService.ErrorState.CERTIFICATE_UNAVAILABLE     // Catch: java.lang.NoSuchFieldError -> L61
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L61
                r2 = 6
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L61
            L4c:
                return
            L4d:
                r4 = move-exception
                goto L14
            L51:
                r4 = move-exception
                goto L1f
            L55:
                r4 = move-exception
                goto L2a
            L59:
                r4 = move-exception
                goto L35
            L5d:
                r4 = move-exception
                goto L40
            L61:
                r4 = move-exception
                goto L4c
            */
            throw new UnsupportedOperationException("Method not decompiled: org.strongswan.android.logic.VpnStateService.AnonymousClass7.m686clinit():void");
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/VpnStateService$ErrorState.class */
    public enum ErrorState {
        NO_ERROR,
        AUTH_FAILED,
        PEER_AUTH_FAILED,
        LOOKUP_FAILED,
        UNREACHABLE,
        GENERIC_ERROR,
        PASSWORD_MISSING,
        CERTIFICATE_UNAVAILABLE
    }

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/VpnStateService$LocalBinder.class */
    public class LocalBinder extends Binder {
        public final VpnStateService this$0;

        public LocalBinder(VpnStateService vpnStateService) {
            this.this$0 = vpnStateService;
        }

        public VpnStateService getService() {
            return this.this$0;
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/VpnStateService$RetryHandler.class */
    public static class RetryHandler extends Handler {
        public WeakReference<VpnStateService> mService;

        public RetryHandler(VpnStateService vpnStateService) {
            this.mService = new WeakReference<>(vpnStateService);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (this.mService.get().mRetryTimeout <= 0) {
                return;
            }
            VpnStateService.access$1222(this.mService.get(), VpnStateService.RETRY_INTERVAL);
            if (this.mService.get().mRetryIn <= 0) {
                this.mService.get().connect(null, false);
                return;
            }
            long uptimeMillis = SystemClock.uptimeMillis();
            long j7 = VpnStateService.RETRY_INTERVAL;
            Iterator it = this.mService.get().mListeners.iterator();
            while (it.hasNext()) {
                ((VpnStateListener) it.next()).stateChanged();
            }
            sendMessageAtTime(obtainMessage(VpnStateService.RETRY_MSG), j7 + uptimeMillis);
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/VpnStateService$RetryTimeoutProvider.class */
    public static class RetryTimeoutProvider {
        private long mRetry;

        private RetryTimeoutProvider() {
        }

        private long getBaseTimeout(ErrorState errorState) {
            int i7 = AnonymousClass7.$SwitchMap$org$strongswan$android$logic$VpnStateService$ErrorState[errorState.ordinal()];
            if (i7 == 2 || i7 == 3 || i7 == 4) {
                return 5000L;
            }
            if (i7 != 5) {
                return i7 != 6 ? 10000L : 5000L;
            }
            return 0L;
        }

        public long getTimeout(ErrorState errorState) {
            double baseTimeout = getBaseTimeout(errorState);
            long j7 = this.mRetry;
            this.mRetry = 1 + j7;
            return Math.min((((long) (Math.pow(2.0d, j7) * baseTimeout)) / 1000) * 1000, VpnStateService.MAX_RETRY_INTERVAL);
        }

        public void reset() {
            this.mRetry = 0L;
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/VpnStateService$State.class */
    public enum State {
        DISABLED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING
    }

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/VpnStateService$VpnStateListener.class */
    public interface VpnStateListener {
        void stateChanged();
    }

    public static /* synthetic */ long access$1222(VpnStateService vpnStateService, long j7) {
        long j8 = vpnStateService.mRetryIn - j7;
        vpnStateService.mRetryIn = j8;
        return j8;
    }

    public static /* synthetic */ long access$308(VpnStateService vpnStateService) {
        long j7 = vpnStateService.mConnectionID;
        vpnStateService.mConnectionID = 1 + j7;
        return j7;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$reconnect$0() {
        resetRetryTimer();
        return Boolean.TRUE;
    }

    private void notifyListeners(Callable<Boolean> callable) {
        this.mHandler.post(new Runnable(this, callable) { // from class: org.strongswan.android.logic.VpnStateService.1
            public final VpnStateService this$0;
            public final Callable val$change;

            {
                this.this$0 = this;
                this.val$change = callable;
            }

            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (((Boolean) this.val$change.call()).booleanValue()) {
                        synchronized (this.this$0.mListeners) {
                            Iterator it = this.this$0.mListeners.iterator();
                            while (it.hasNext()) {
                                ((VpnStateListener) it.next()).stateChanged();
                            }
                        }
                    }
                } catch (Exception e8) {
                    e8.printStackTrace();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetRetryTimer() {
        this.mRetryTimeout = 0L;
        this.mRetryIn = 0L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRetryTimer(ErrorState errorState) {
        long timeout = this.mTimeoutProvider.getTimeout(errorState);
        this.mRetryIn = timeout;
        this.mRetryTimeout = timeout;
        if (timeout <= 0) {
            return;
        }
        Handler handler = this.mHandler;
        handler.sendMessageAtTime(handler.obtainMessage(RETRY_MSG), SystemClock.uptimeMillis() + RETRY_INTERVAL);
    }

    public void addRemediationInstruction(RemediationInstruction remediationInstruction) {
        this.mHandler.post(new Runnable(this, remediationInstruction) { // from class: org.strongswan.android.logic.VpnStateService.6
            public final VpnStateService this$0;
            public final RemediationInstruction val$instruction;

            {
                this.this$0 = this;
                this.val$instruction = remediationInstruction;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.mRemediationInstructions.add(this.val$instruction);
            }
        });
    }

    public void connect(Bundle bundle, boolean z7) {
        Intent intent = new Intent(getApplicationContext(), (Class<?>) CharonVpnService.class);
        Bundle bundle2 = bundle;
        if (bundle == null) {
            bundle2 = new Bundle();
            bundle2.putString(VpnProfileDataSource.KEY_UUID, this.mProfile.getUUID().toString());
            bundle2.putString(VpnProfileDataSource.KEY_PASSWORD, this.mProfile.getPassword());
        }
        if (z7) {
            this.mTimeoutProvider.reset();
        } else {
            bundle2.putBoolean(CharonVpnService.KEY_IS_RETRY, true);
        }
        intent.putExtras(bundle2);
        startService(intent);
    }

    public void disconnect() {
        resetRetryTimer();
        setError(ErrorState.NO_ERROR);
        Context applicationContext = getApplicationContext();
        Intent intent = new Intent(applicationContext, (Class<?>) CharonVpnService.class);
        intent.setAction(CharonVpnService.DISCONNECT_ACTION);
        applicationContext.startService(intent);
    }

    public long getConnectionID() {
        return this.mConnectionID;
    }

    public ErrorState getErrorState() {
        return this.mError;
    }

    public int getErrorText() {
        switch (AnonymousClass7.$SwitchMap$org$strongswan$android$logic$VpnStateService$ErrorState[this.mError.ordinal()]) {
            case 1:
                return this.mImcState == ImcState.BLOCK ? 2131755262 : 2131755263;
            case 2:
                return 2131755276;
            case 3:
                return 2131755273;
            case 4:
                return 2131755279;
            case 5:
                return 2131755275;
            case 6:
                return 2131755265;
            default:
                return 2131755269;
        }
    }

    public ImcState getImcState() {
        return this.mImcState;
    }

    public VpnProfile getProfile() {
        return this.mProfile;
    }

    public List<RemediationInstruction> getRemediationInstructions() {
        return Collections.unmodifiableList(this.mRemediationInstructions);
    }

    public int getRetryIn() {
        return (int) (this.mRetryIn / 1000);
    }

    public int getRetryTimeout() {
        return (int) (this.mRetryTimeout / 1000);
    }

    public State getState() {
        return this.mState;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public void onCreate() {
        this.mHandler = new RetryHandler(this);
    }

    @Override // android.app.Service
    public void onDestroy() {
    }

    public void reconnect() {
        VpnProfile vpnProfile = this.mProfile;
        if (vpnProfile == null) {
            return;
        }
        if (!vpnProfile.getVpnType().has(VpnType.VpnTypeFeature.USER_PASS) || (this.mProfile.getPassword() != null && this.mError != ErrorState.AUTH_FAILED)) {
            connect(null, true);
            return;
        }
        Intent intent = new Intent(this, (Class<?>) MainActivity.class);
        intent.addFlags(268435456);
        intent.setAction("org.strongswan.android.action.START_PROFILE");
        intent.putExtra("org.strongswan.android.VPN_PROFILE_ID", this.mProfile.getUUID().toString());
        startActivity(intent);
        notifyListeners(new Callable(this) { // from class: org.strongswan.android.logic.b

            /* renamed from: a, reason: collision with root package name */
            public final VpnStateService f5257a;

            {
                this.f5257a = this;
            }

            @Override // java.util.concurrent.Callable
            public final Object call() {
                Boolean lambda$reconnect$0;
                lambda$reconnect$0 = this.f5257a.lambda$reconnect$0();
                return lambda$reconnect$0;
            }
        });
    }

    public void registerListener(VpnStateListener vpnStateListener) {
        synchronized (this.mListeners) {
            this.mListeners.add(vpnStateListener);
        }
    }

    public void setError(ErrorState errorState) {
        notifyListeners(new Callable<Boolean>(this, errorState) { // from class: org.strongswan.android.logic.VpnStateService.4
            public final VpnStateService this$0;
            public final ErrorState val$error;

            {
                this.this$0 = this;
                this.val$error = errorState;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Boolean call() {
                if (this.this$0.mError == this.val$error) {
                    return Boolean.FALSE;
                }
                ErrorState errorState2 = this.this$0.mError;
                ErrorState errorState3 = ErrorState.NO_ERROR;
                if (errorState2 == errorState3) {
                    this.this$0.setRetryTimer(this.val$error);
                } else if (this.val$error == errorState3) {
                    this.this$0.resetRetryTimer();
                }
                this.this$0.mError = this.val$error;
                return Boolean.TRUE;
            }
        });
    }

    public void setImcState(ImcState imcState) {
        notifyListeners(new Callable<Boolean>(this, imcState) { // from class: org.strongswan.android.logic.VpnStateService.5
            public final VpnStateService this$0;
            public final ImcState val$state;

            {
                this.this$0 = this;
                this.val$state = imcState;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Boolean call() {
                if (this.val$state == ImcState.UNKNOWN) {
                    this.this$0.mRemediationInstructions.clear();
                }
                ImcState imcState2 = this.this$0.mImcState;
                ImcState imcState3 = this.val$state;
                if (imcState2 == imcState3) {
                    return Boolean.FALSE;
                }
                this.this$0.mImcState = imcState3;
                return Boolean.TRUE;
            }
        });
    }

    public void setState(State state) {
        notifyListeners(new Callable<Boolean>(this, state) { // from class: org.strongswan.android.logic.VpnStateService.3
            public final VpnStateService this$0;
            public final State val$state;

            {
                this.this$0 = this;
                this.val$state = state;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Boolean call() {
                if (this.val$state == State.CONNECTED) {
                    Log.d(getClass().toString(), "Strongswan Connected");
                    this.this$0.mTimeoutProvider.reset();
                }
                State state2 = this.this$0.mState;
                State state3 = this.val$state;
                if (state2 == state3) {
                    return Boolean.FALSE;
                }
                this.this$0.mState = state3;
                return Boolean.TRUE;
            }
        });
    }

    public void startConnection(VpnProfile vpnProfile) {
        notifyListeners(new Callable<Boolean>(this, vpnProfile) { // from class: org.strongswan.android.logic.VpnStateService.2
            public final VpnStateService this$0;
            public final VpnProfile val$profile;

            {
                this.this$0 = this;
                this.val$profile = vpnProfile;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Boolean call() {
                this.this$0.resetRetryTimer();
                VpnStateService.access$308(this.this$0);
                this.this$0.mProfile = this.val$profile;
                this.this$0.mState = State.CONNECTING;
                this.this$0.mError = ErrorState.NO_ERROR;
                this.this$0.mImcState = ImcState.UNKNOWN;
                this.this$0.mRemediationInstructions.clear();
                return Boolean.TRUE;
            }
        });
    }

    public void unregisterListener(VpnStateListener vpnStateListener) {
        synchronized (this.mListeners) {
            this.mListeners.remove(vpnStateListener);
        }
    }
}
