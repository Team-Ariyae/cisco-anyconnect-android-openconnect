package org.strongswan.android.logic;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.VpnService;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.security.KeyChain;
import android.system.OsConstants;
import android.util.Log;
import com.tehvpn.Activities.ConnectedActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import org.strongswan.android.data.VpnProfile;
import org.strongswan.android.data.VpnProfileDataSource;
import org.strongswan.android.data.VpnType;
import org.strongswan.android.logic.VpnStateService;
import org.strongswan.android.logic.imc.ImcState;
import org.strongswan.android.logic.imc.RemediationInstruction;
import org.strongswan.android.utils.IPRange;
import org.strongswan.android.utils.IPRangeSet;
import org.strongswan.android.utils.SettingsWriter;
import org.strongswan.android.utils.Utils;
import q.g;
import w2.e;
import x2.d;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/CharonVpnService.class */
public class CharonVpnService extends VpnService implements Runnable, VpnStateService.VpnStateListener {
    public static final String DISCONNECT_ACTION = "org.strongswan.android.CharonVpnService.DISCONNECT";
    public static final String KEY_IS_RETRY = "retry";
    public static final String LOG_FILE = "charon.log";
    private static final String NOTIFICATION_CHANNEL = "org.strongswan.android.CharonVpnService.VPN_STATE_NOTIFICATION";
    public static final int STATE_AUTH_ERROR = 3;
    public static final int STATE_CERTIFICATE_UNAVAILABLE = 7;
    public static final int STATE_CHILD_SA_DOWN = 2;
    public static final int STATE_CHILD_SA_UP = 1;
    public static final int STATE_GENERIC_ERROR = 8;
    public static final int STATE_LOOKUP_ERROR = 5;
    public static final int STATE_PEER_AUTH_ERROR = 4;
    public static final int STATE_UNREACHABLE_ERROR = 6;
    private static final String TAG = "CharonVpnService";
    private static final String VPN_SERVICE_ACTION = "android.net.VpnService";
    public static final int VPN_STATE_NOTIFICATION_ID = 1;
    private String mAppDir;
    private Thread mConnectionHandler;
    private volatile String mCurrentCertificateAlias;
    private VpnProfile mCurrentProfile;
    private volatile String mCurrentUserCertificateAlias;
    private VpnProfileDataSource mDataSource;
    private Handler mHandler;
    private volatile boolean mIsDisconnecting;
    private String mLogFile;
    private VpnProfile mNextProfile;
    private volatile boolean mProfileUpdated;
    private VpnStateService mService;
    private volatile boolean mShowNotification;
    private volatile boolean mTerminate;
    public final Handler handler = new Handler(Looper.getMainLooper());
    private BuilderAdapter mBuilderAdapter = new BuilderAdapter(this);
    private final Object mServiceLock = new Object();
    private final ServiceConnection mServiceConnection = new ServiceConnection(this) { // from class: org.strongswan.android.logic.CharonVpnService.1
        public final CharonVpnService this$0;

        {
            this.this$0 = this;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (this.this$0.mServiceLock) {
                this.this$0.mService = ((VpnStateService.LocalBinder) iBinder).getService();
            }
            this.this$0.mService.registerListener(this.this$0);
            this.this$0.mConnectionHandler.start();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (this.this$0.mServiceLock) {
                this.this$0.mService = null;
            }
        }
    };
    public Runnable delayedSwitch = new x.a(6, e.f6502a);

    /* renamed from: org.strongswan.android.logic.CharonVpnService$2, reason: invalid class name */
    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/CharonVpnService$2.class */
    public static /* synthetic */ class AnonymousClass2 {
        public static final int[] $SwitchMap$org$strongswan$android$data$VpnProfile$SelectedAppsHandling;

        /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxRuntimeException: Can't find top splitter block for handler:B:15:0x002f
            	at jadx.core.utils.BlockUtils.getTopSplitterForHandler(BlockUtils.java:1166)
            	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:1022)
            	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:55)
            */
        static {
            /*
                org.strongswan.android.data.VpnProfile$SelectedAppsHandling[] r0 = org.strongswan.android.data.VpnProfile.SelectedAppsHandling.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                r4 = r0
                r0 = r4
                org.strongswan.android.logic.CharonVpnService.AnonymousClass2.$SwitchMap$org$strongswan$android$data$VpnProfile$SelectedAppsHandling = r0
                r0 = r4
                org.strongswan.android.data.VpnProfile$SelectedAppsHandling r1 = org.strongswan.android.data.VpnProfile.SelectedAppsHandling.SELECTED_APPS_DISABLE     // Catch: java.lang.NoSuchFieldError -> L2b
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L2b
                r2 = 1
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L2b
            L14:
                int[] r0 = org.strongswan.android.logic.CharonVpnService.AnonymousClass2.$SwitchMap$org$strongswan$android$data$VpnProfile$SelectedAppsHandling     // Catch: java.lang.NoSuchFieldError -> L2b java.lang.NoSuchFieldError -> L2f
                org.strongswan.android.data.VpnProfile$SelectedAppsHandling r1 = org.strongswan.android.data.VpnProfile.SelectedAppsHandling.SELECTED_APPS_EXCLUDE     // Catch: java.lang.NoSuchFieldError -> L2f
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L2f
                r2 = 2
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L2f
            L1f:
                int[] r0 = org.strongswan.android.logic.CharonVpnService.AnonymousClass2.$SwitchMap$org$strongswan$android$data$VpnProfile$SelectedAppsHandling     // Catch: java.lang.NoSuchFieldError -> L2f java.lang.NoSuchFieldError -> L33
                org.strongswan.android.data.VpnProfile$SelectedAppsHandling r1 = org.strongswan.android.data.VpnProfile.SelectedAppsHandling.SELECTED_APPS_ONLY     // Catch: java.lang.NoSuchFieldError -> L33
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L33
                r2 = 3
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L33
            L2a:
                return
            L2b:
                r4 = move-exception
                goto L14
            L2f:
                r4 = move-exception
                goto L1f
            L33:
                r4 = move-exception
                goto L2a
            */
            throw new UnsupportedOperationException("Method not decompiled: org.strongswan.android.logic.CharonVpnService.AnonymousClass2.m681clinit():void");
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/CharonVpnService$BuilderAdapter.class */
    public class BuilderAdapter {
        private VpnService.Builder mBuilder;
        private BuilderCache mCache;
        private PacketDropper mDropper = new PacketDropper();
        private BuilderCache mEstablishedCache;
        private VpnProfile mProfile;
        public final CharonVpnService this$0;

        /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/CharonVpnService$BuilderAdapter$PacketDropper.class */
        public class PacketDropper implements Runnable {
            private ParcelFileDescriptor mFd;
            private Thread mThread;
            public final BuilderAdapter this$1;

            private PacketDropper(BuilderAdapter builderAdapter) {
                this.this$1 = builderAdapter;
            }

            @Override // java.lang.Runnable
            public void run() {
                synchronized (this) {
                    try {
                        try {
                            FileInputStream fileInputStream = new FileInputStream(this.mFd.getFileDescriptor());
                            ByteBuffer allocate = ByteBuffer.allocate(this.this$1.mCache.mMtu);
                            while (true) {
                                if (Build.VERSION.SDK_INT >= 24) {
                                    int read = fileInputStream.getChannel().read(allocate);
                                    allocate.clear();
                                    if (read < 0) {
                                        break;
                                    }
                                } else {
                                    boolean z7 = true;
                                    if (fileInputStream.available() > 0) {
                                        int read2 = fileInputStream.read(allocate.array());
                                        allocate.clear();
                                        if (read2 < 0 || Thread.interrupted()) {
                                            break;
                                        } else {
                                            z7 = false;
                                        }
                                    }
                                    if (z7) {
                                        Thread.sleep(250L);
                                    }
                                }
                            }
                        } catch (InterruptedException | ClosedByInterruptException e8) {
                        }
                    } catch (IOException e9) {
                        e9.printStackTrace();
                    }
                }
            }

            public void start(ParcelFileDescriptor parcelFileDescriptor) {
                this.mFd = parcelFileDescriptor;
                Thread thread = new Thread(this);
                this.mThread = thread;
                thread.start();
            }

            public void stop() {
                if (this.mFd != null) {
                    try {
                        this.mThread.interrupt();
                        this.mThread.join();
                        this.mFd.close();
                    } catch (IOException | InterruptedException e8) {
                        e8.printStackTrace();
                    }
                    this.mFd = null;
                }
            }
        }

        public BuilderAdapter(CharonVpnService charonVpnService) {
            this.this$0 = charonVpnService;
        }

        private VpnService.Builder createBuilder(String str) {
            VpnService.Builder builder = new VpnService.Builder(this.this$0);
            builder.setSession(str);
            Context applicationContext = this.this$0.getApplicationContext();
            d.a(applicationContext, builder);
            builder.setConfigureIntent(PendingIntent.getActivity(applicationContext, 0, new Intent(applicationContext, (Class<?>) ConnectedActivity.class), 201326592));
            return builder;
        }

        private ParcelFileDescriptor establishIntern() {
            synchronized (this) {
                try {
                    this.mCache.applyData(this.mBuilder);
                    ParcelFileDescriptor establish = this.mBuilder.establish();
                    if (establish != null) {
                        closeBlocking();
                    }
                    if (establish == null) {
                        return null;
                    }
                    this.mBuilder = createBuilder(this.mProfile.getName());
                    this.mEstablishedCache = this.mCache;
                    this.mCache = new BuilderCache(this.this$0, this.mProfile);
                    return establish;
                } catch (Exception e8) {
                    e8.printStackTrace();
                    return null;
                }
            }
        }

        public boolean addAddress(String str, int i7) {
            boolean z7;
            synchronized (this) {
                try {
                    this.mCache.addAddress(str, i7);
                    z7 = true;
                } catch (IllegalArgumentException e8) {
                    z7 = false;
                }
            }
            return z7;
        }

        public boolean addDnsServer(String str) {
            boolean z7;
            synchronized (this) {
                try {
                    this.mCache.addDnsServer(str);
                    z7 = true;
                } catch (IllegalArgumentException e8) {
                    z7 = false;
                }
            }
            return z7;
        }

        public boolean addRoute(String str, int i7) {
            boolean z7;
            synchronized (this) {
                try {
                    this.mCache.addRoute(str, i7);
                    z7 = true;
                } catch (IllegalArgumentException e8) {
                    z7 = false;
                }
            }
            return z7;
        }

        public boolean addSearchDomain(String str) {
            boolean z7;
            synchronized (this) {
                try {
                    this.mBuilder.addSearchDomain(str);
                    z7 = true;
                } catch (IllegalArgumentException e8) {
                    z7 = false;
                }
            }
            return z7;
        }

        public void closeBlocking() {
            synchronized (this) {
                this.mDropper.stop();
            }
        }

        public int establish() {
            int detachFd;
            synchronized (this) {
                ParcelFileDescriptor establishIntern = establishIntern();
                detachFd = establishIntern != null ? establishIntern.detachFd() : -1;
            }
            return detachFd;
        }

        @TargetApi(21)
        public void establishBlocking() {
            synchronized (this) {
                this.mCache.addAddress("172.16.252.1", 32);
                this.mCache.addAddress("fd00::fd02:1", 128);
                this.mCache.addRoute("0.0.0.0", 0);
                this.mCache.addRoute("::", 0);
                this.mBuilder.addDnsServer("8.8.8.8");
                this.mBuilder.addDnsServer("2001:4860:4860::8888");
                this.mBuilder.setBlocking(true);
                ParcelFileDescriptor establishIntern = establishIntern();
                if (establishIntern != null) {
                    this.mDropper.start(establishIntern);
                }
            }
        }

        public int establishNoDns() {
            synchronized (this) {
                if (this.mEstablishedCache == null) {
                    return -1;
                }
                try {
                    VpnService.Builder createBuilder = createBuilder(this.mProfile.getName());
                    this.mEstablishedCache.applyData(createBuilder);
                    ParcelFileDescriptor establish = createBuilder.establish();
                    if (establish == null) {
                        return -1;
                    }
                    return establish.detachFd();
                } catch (Exception e8) {
                    e8.printStackTrace();
                    return -1;
                }
            }
        }

        public boolean setMtu(int i7) {
            boolean z7;
            synchronized (this) {
                try {
                    this.mCache.setMtu(i7);
                    z7 = true;
                } catch (IllegalArgumentException e8) {
                    z7 = false;
                }
            }
            return z7;
        }

        public void setProfile(VpnProfile vpnProfile) {
            synchronized (this) {
                this.mProfile = vpnProfile;
                this.mBuilder = createBuilder(vpnProfile.getName());
                this.mCache = new BuilderCache(this.this$0, this.mProfile);
            }
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/CharonVpnService$BuilderCache.class */
    public class BuilderCache {
        private final VpnProfile.SelectedAppsHandling mAppHandling;
        private boolean mDnsServersConfigured;
        private final IPRangeSet mExcludedSubnets;
        private boolean mIPv4Seen;
        private boolean mIPv6Seen;
        private int mMtu;
        private final SortedSet<String> mSelectedApps;
        private final int mSplitTunneling;
        public final CharonVpnService this$0;
        private final List<IPRange> mAddresses = new ArrayList();
        private final List<IPRange> mRoutesIPv4 = new ArrayList();
        private final List<IPRange> mRoutesIPv6 = new ArrayList();
        private final IPRangeSet mIncludedSubnetsv4 = new IPRangeSet();
        private final IPRangeSet mIncludedSubnetsv6 = new IPRangeSet();
        private final List<InetAddress> mDnsServers = new ArrayList();

        /* JADX WARN: Removed duplicated region for block: B:29:0x0133  */
        /* JADX WARN: Removed duplicated region for block: B:44:0x0182  */
        /* JADX WARN: Removed duplicated region for block: B:48:0x0189  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public BuilderCache(org.strongswan.android.logic.CharonVpnService r5, org.strongswan.android.data.VpnProfile r6) {
            /*
                Method dump skipped, instructions count: 404
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: org.strongswan.android.logic.CharonVpnService.BuilderCache.<init>(org.strongswan.android.logic.CharonVpnService, org.strongswan.android.data.VpnProfile):void");
        }

        private boolean isIPv6(String str) {
            InetAddress parseInetAddress = Utils.parseInetAddress(str);
            return !(parseInetAddress instanceof Inet4Address) && (parseInetAddress instanceof Inet6Address);
        }

        public void addAddress(String str, int i7) {
            try {
                this.mAddresses.add(new IPRange(str, i7));
                recordAddressFamily(str);
            } catch (UnknownHostException e8) {
                e8.printStackTrace();
            }
        }

        public void addDnsServer(String str) {
            if (this.mDnsServersConfigured) {
                return;
            }
            try {
                this.mDnsServers.add(Utils.parseInetAddress(str));
                recordAddressFamily(str);
            } catch (UnknownHostException e8) {
                e8.printStackTrace();
            }
        }

        public void addRoute(String str, int i7) {
            IPRange iPRange;
            List<IPRange> list;
            try {
                if (isIPv6(str)) {
                    List<IPRange> list2 = this.mRoutesIPv6;
                    iPRange = new IPRange(str, i7);
                    list = list2;
                } else {
                    List<IPRange> list3 = this.mRoutesIPv4;
                    iPRange = new IPRange(str, i7);
                    list = list3;
                }
                list.add(iPRange);
            } catch (UnknownHostException e8) {
                e8.printStackTrace();
            }
        }

        @TargetApi(21)
        public void applyData(VpnService.Builder builder) {
            for (IPRange iPRange : this.mAddresses) {
                builder.addAddress(iPRange.getFrom(), iPRange.getPrefix().intValue());
            }
            Iterator<InetAddress> it = this.mDnsServers.iterator();
            while (it.hasNext()) {
                builder.addDnsServer(it.next());
            }
            if ((this.mSplitTunneling & 1) == 0) {
                if (this.mIPv4Seen) {
                    IPRangeSet iPRangeSet = new IPRangeSet();
                    if (this.mIncludedSubnetsv4.size() > 0) {
                        iPRangeSet.add(this.mIncludedSubnetsv4);
                    } else {
                        iPRangeSet.addAll(this.mRoutesIPv4);
                    }
                    iPRangeSet.remove(this.mExcludedSubnets);
                    for (IPRange iPRange2 : iPRangeSet.subnets()) {
                        try {
                            builder.addRoute(iPRange2.getFrom(), iPRange2.getPrefix().intValue());
                        } catch (IllegalArgumentException e8) {
                            if (!iPRange2.getFrom().isMulticastAddress()) {
                                throw e8;
                            }
                        }
                    }
                } else {
                    builder.allowFamily(OsConstants.AF_INET);
                }
            } else if (this.mIPv4Seen) {
                builder.addRoute("0.0.0.0", 0);
            }
            if ((this.mSplitTunneling & 2) == 0) {
                if (this.mIPv6Seen) {
                    IPRangeSet iPRangeSet2 = new IPRangeSet();
                    if (this.mIncludedSubnetsv6.size() > 0) {
                        iPRangeSet2.add(this.mIncludedSubnetsv6);
                    } else {
                        iPRangeSet2.addAll(this.mRoutesIPv6);
                    }
                    iPRangeSet2.remove(this.mExcludedSubnets);
                    for (IPRange iPRange3 : iPRangeSet2.subnets()) {
                        try {
                            builder.addRoute(iPRange3.getFrom(), iPRange3.getPrefix().intValue());
                        } catch (IllegalArgumentException e9) {
                            if (!iPRange3.getFrom().isMulticastAddress()) {
                                throw e9;
                            }
                        }
                    }
                } else {
                    builder.allowFamily(OsConstants.AF_INET6);
                }
            } else if (this.mIPv6Seen) {
                builder.addRoute("::", 0);
            }
            if (this.mSelectedApps.size() > 0) {
                int i7 = AnonymousClass2.$SwitchMap$org$strongswan$android$data$VpnProfile$SelectedAppsHandling[this.mAppHandling.ordinal()];
                if (i7 == 2) {
                    Iterator<String> it2 = this.mSelectedApps.iterator();
                    while (it2.hasNext()) {
                        try {
                            builder.addDisallowedApplication(it2.next());
                        } catch (PackageManager.NameNotFoundException e10) {
                        }
                    }
                } else if (i7 == 3) {
                    Iterator<String> it3 = this.mSelectedApps.iterator();
                    while (it3.hasNext()) {
                        try {
                            builder.addAllowedApplication(it3.next());
                        } catch (PackageManager.NameNotFoundException e11) {
                        }
                    }
                }
            }
            builder.setMtu(this.mMtu);
        }

        public void recordAddressFamily(String str) {
            try {
                if (isIPv6(str)) {
                    this.mIPv6Seen = true;
                } else {
                    this.mIPv4Seen = true;
                }
            } catch (UnknownHostException e8) {
                e8.printStackTrace();
            }
        }

        public void setMtu(int i7) {
            this.mMtu = i7;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL, getString(2131755566), 2);
            notificationChannel.setDescription(getString(2131755565));
            notificationChannel.setLockscreenVisibility(-1);
            notificationChannel.setShowBadge(false);
            ((NotificationManager) getSystemService(NotificationManager.class)).createNotificationChannel(notificationChannel);
        }
    }

    private static String getAndroidVersion() {
        StringBuilder r7 = androidx.activity.result.a.r("Android ");
        r7.append(Build.VERSION.RELEASE);
        r7.append(" - ");
        r7.append(Build.DISPLAY);
        String sb = r7.toString();
        String str = sb;
        if (Build.VERSION.SDK_INT >= 23) {
            StringBuilder d8 = g.d(sb, "/");
            d8.append(Build.VERSION.SECURITY_PATCH);
            str = d8.toString();
        }
        return str;
    }

    private static String getDeviceString() {
        return Build.MODEL + " - " + Build.BRAND + "/" + Build.PRODUCT + "/" + Build.MANUFACTURER;
    }

    private byte[][] getTrustedCertificates() {
        ArrayList arrayList = new ArrayList();
        TrustedCertificateManager load = TrustedCertificateManager.getInstance().load();
        try {
            String str = this.mCurrentCertificateAlias;
            if (str != null) {
                X509Certificate cACertificateFromAlias = load.getCACertificateFromAlias(str);
                if (cACertificateFromAlias == null) {
                    return null;
                }
                arrayList.add(cACertificateFromAlias.getEncoded());
            } else {
                Iterator<X509Certificate> it = load.getAllCACertificates().values().iterator();
                while (it.hasNext()) {
                    arrayList.add(it.next().getEncoded());
                }
            }
            return (byte[][]) arrayList.toArray((Object[]) new byte[arrayList.size()]);
        } catch (CertificateEncodingException e8) {
            e8.printStackTrace();
            return null;
        }
    }

    private byte[][] getUserCertificate() {
        ArrayList arrayList = new ArrayList();
        X509Certificate[] certificateChain = KeyChain.getCertificateChain(getApplicationContext(), this.mCurrentUserCertificateAlias);
        if (certificateChain == null || certificateChain.length == 0) {
            return null;
        }
        for (X509Certificate x509Certificate : certificateChain) {
            arrayList.add(x509Certificate.getEncoded());
        }
        return (byte[][]) arrayList.toArray((Object[]) new byte[arrayList.size()]);
    }

    private PrivateKey getUserKey() {
        return KeyChain.getPrivateKey(getApplicationContext(), this.mCurrentUserCertificateAlias);
    }

    private void setError(VpnStateService.ErrorState errorState) {
        synchronized (this.mServiceLock) {
            VpnStateService vpnStateService = this.mService;
            if (vpnStateService != null) {
                vpnStateService.setError(errorState);
            }
        }
    }

    private void setErrorDisconnect(VpnStateService.ErrorState errorState) {
        synchronized (this.mServiceLock) {
            if (this.mService != null && !this.mIsDisconnecting) {
                this.mService.setError(errorState);
            }
        }
    }

    private void setImcState(ImcState imcState) {
        synchronized (this.mServiceLock) {
            VpnStateService vpnStateService = this.mService;
            if (vpnStateService != null) {
                vpnStateService.setImcState(imcState);
            }
        }
    }

    private void setNextProfile(VpnProfile vpnProfile) {
        synchronized (this) {
            this.mNextProfile = vpnProfile;
            this.mProfileUpdated = true;
            notifyAll();
        }
    }

    private void setState(VpnStateService.State state) {
        synchronized (this.mServiceLock) {
            VpnStateService vpnStateService = this.mService;
            if (vpnStateService != null) {
                vpnStateService.setState(state);
            }
        }
    }

    private void startConnection(VpnProfile vpnProfile) {
        synchronized (this.mServiceLock) {
            VpnStateService vpnStateService = this.mService;
            if (vpnStateService != null) {
                vpnStateService.startConnection(vpnProfile);
            }
        }
    }

    private void stopCurrentConnection() {
        synchronized (this) {
            VpnProfile vpnProfile = this.mNextProfile;
            if (vpnProfile != null) {
                this.mBuilderAdapter.setProfile(vpnProfile);
                this.mBuilderAdapter.establishBlocking();
            }
            if (this.mCurrentProfile != null) {
                setState(VpnStateService.State.DISCONNECTING);
                this.mIsDisconnecting = true;
                SimpleFetcher.disable();
                deinitializeCharon();
                Log.i(TAG, "charon stopped");
                this.mCurrentProfile = null;
                if (this.mNextProfile == null) {
                    this.mBuilderAdapter.closeBlocking();
                }
            }
        }
    }

    public void addRemediationInstruction(String str) {
        for (RemediationInstruction remediationInstruction : RemediationInstruction.fromXml(str)) {
            synchronized (this.mServiceLock) {
                VpnStateService vpnStateService = this.mService;
                if (vpnStateService != null) {
                    vpnStateService.addRemediationInstruction(remediationInstruction);
                }
            }
        }
    }

    public native void deinitializeCharon();

    public native boolean initializeCharon(BuilderAdapter builderAdapter, String str, String str2, boolean z7);

    public native void initiate(String str);

    @Override // android.app.Service
    public void onCreate() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFilesDir().getAbsolutePath());
        this.mLogFile = g.c(sb, File.separator, LOG_FILE);
        this.mAppDir = getFilesDir().getAbsolutePath();
        this.mHandler = new Handler();
        VpnProfileDataSource vpnProfileDataSource = new VpnProfileDataSource(this);
        this.mDataSource = vpnProfileDataSource;
        vpnProfileDataSource.open();
        this.mConnectionHandler = new Thread(this);
        bindService(new Intent(this, (Class<?>) VpnStateService.class), this.mServiceConnection, 1);
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mTerminate = true;
        setNextProfile(null);
        try {
            this.mConnectionHandler.join();
        } catch (InterruptedException e8) {
            e8.printStackTrace();
        }
        VpnStateService vpnStateService = this.mService;
        if (vpnStateService != null) {
            vpnStateService.unregisterListener(this);
            unbindService(this.mServiceConnection);
        }
        this.mDataSource.close();
    }

    @Override // android.net.VpnService
    public void onRevoke() {
        setNextProfile(null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x0046, code lost:
    
        if (r0.equals(org.strongswan.android.utils.Constants.PREF_DEFAULT_VPN_PROFILE_MRU) != false) goto L10;
     */
    @Override // android.app.Service
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int onStartCommand(android.content.Intent r5, int r6, int r7) {
        /*
            Method dump skipped, instructions count: 239
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.strongswan.android.logic.CharonVpnService.onStartCommand(android.content.Intent, int, int):int");
    }

    @Override // java.lang.Runnable
    public void run() {
        while (true) {
            synchronized (this) {
                while (!this.mProfileUpdated) {
                    try {
                        wait();
                    } catch (InterruptedException e8) {
                        stopCurrentConnection();
                        setState(VpnStateService.State.DISABLED);
                    }
                }
                this.mProfileUpdated = false;
                stopCurrentConnection();
                VpnProfile vpnProfile = this.mNextProfile;
                if (vpnProfile == null) {
                    setState(VpnStateService.State.DISABLED);
                    if (this.mTerminate) {
                        return;
                    }
                } else {
                    this.mCurrentProfile = vpnProfile;
                    this.mNextProfile = null;
                    this.mCurrentCertificateAlias = vpnProfile.getCertificateAlias();
                    this.mCurrentUserCertificateAlias = this.mCurrentProfile.getUserCertificateAlias();
                    startConnection(this.mCurrentProfile);
                    this.mIsDisconnecting = false;
                    SimpleFetcher.enable();
                    this.mBuilderAdapter.setProfile(this.mCurrentProfile);
                    if (initializeCharon(this.mBuilderAdapter, this.mLogFile, this.mAppDir, this.mCurrentProfile.getVpnType().has(VpnType.VpnTypeFeature.BYOD))) {
                        Log.i(TAG, "charon started");
                        if (this.mCurrentProfile.getVpnType().has(VpnType.VpnTypeFeature.USER_PASS) && this.mCurrentProfile.getPassword() == null) {
                            setError(VpnStateService.ErrorState.PASSWORD_MISSING);
                        } else {
                            SettingsWriter settingsWriter = new SettingsWriter();
                            settingsWriter.setValue("global.language", Locale.getDefault().getLanguage());
                            settingsWriter.setValue("global.mtu", this.mCurrentProfile.getMTU());
                            settingsWriter.setValue("global.nat_keepalive", this.mCurrentProfile.getNATKeepAlive());
                            settingsWriter.setValue("global.rsa_pss", Boolean.valueOf((this.mCurrentProfile.getFlags().intValue() & 16) != 0));
                            settingsWriter.setValue("global.crl", Boolean.valueOf((this.mCurrentProfile.getFlags().intValue() & 2) == 0));
                            settingsWriter.setValue("global.ocsp", Boolean.valueOf((this.mCurrentProfile.getFlags().intValue() & 4) == 0));
                            settingsWriter.setValue("connection.type", this.mCurrentProfile.getVpnType().getIdentifier());
                            settingsWriter.setValue("connection.server", this.mCurrentProfile.getGateway());
                            settingsWriter.setValue("connection.port", this.mCurrentProfile.getPort());
                            settingsWriter.setValue("connection.username", this.mCurrentProfile.getUsername());
                            settingsWriter.setValue("connection.password", this.mCurrentProfile.getPassword());
                            settingsWriter.setValue("connection.local_id", this.mCurrentProfile.getLocalId());
                            settingsWriter.setValue("connection.remote_id", this.mCurrentProfile.getRemoteId());
                            settingsWriter.setValue("connection.certreq", Boolean.valueOf((this.mCurrentProfile.getFlags().intValue() & 1) == 0));
                            boolean z7 = false;
                            if ((this.mCurrentProfile.getFlags().intValue() & 8) != 0) {
                                z7 = true;
                            }
                            settingsWriter.setValue("connection.strict_revocation", Boolean.valueOf(z7));
                            settingsWriter.setValue("connection.ike_proposal", this.mCurrentProfile.getIkeProposal());
                            settingsWriter.setValue("connection.esp_proposal", this.mCurrentProfile.getEspProposal());
                            initiate(settingsWriter.serialize());
                        }
                    } else {
                        Log.e(TAG, "failed to start charon");
                        setError(VpnStateService.ErrorState.GENERIC_ERROR);
                        setState(VpnStateService.State.DISABLED);
                        this.mCurrentProfile = null;
                    }
                }
            }
        }
    }

    @Override // org.strongswan.android.logic.VpnStateService.VpnStateListener
    public void stateChanged() {
    }

    public void updateImcState(int i7) {
        ImcState fromValue = ImcState.fromValue(i7);
        if (fromValue != null) {
            setImcState(fromValue);
        }
    }

    public void updateStatus(int i7) {
        VpnStateService.ErrorState errorState;
        switch (i7) {
            case 1:
                setState(VpnStateService.State.CONNECTED);
                try {
                    this.handler.postDelayed(this.delayedSwitch, 2000L);
                    return;
                } catch (Exception e8) {
                    return;
                }
            case 2:
                if (this.mIsDisconnecting) {
                    return;
                }
                setState(VpnStateService.State.CONNECTING);
                return;
            case 3:
                errorState = VpnStateService.ErrorState.AUTH_FAILED;
                break;
            case 4:
                errorState = VpnStateService.ErrorState.PEER_AUTH_FAILED;
                break;
            case 5:
                errorState = VpnStateService.ErrorState.LOOKUP_FAILED;
                break;
            case 6:
                errorState = VpnStateService.ErrorState.UNREACHABLE;
                break;
            case 7:
                errorState = VpnStateService.ErrorState.CERTIFICATE_UNAVAILABLE;
                break;
            case 8:
                errorState = VpnStateService.ErrorState.GENERIC_ERROR;
                break;
            default:
                Log.e(TAG, "Unknown status code received");
                return;
        }
        setErrorDisconnect(errorState);
    }
}
