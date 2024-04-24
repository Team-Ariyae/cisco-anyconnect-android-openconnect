package app.openconnect.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import app.openconnect.AuthFormHandler;
import app.openconnect.Credentials;
import app.openconnect.VpnProfile;
import io.github.inflationx.calligraphy3.BuildConfig;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infradead.libopenconnect.LibOpenConnect;
import org.strongswan.android.data.VpnProfileDataSource;
import org.xbill.DNS.TSIG;
import q.g;
import r.i;
import s2.b;
import s2.c;
import x2.d;
import x2.e;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/OpenConnectManagementThread.class */
public class OpenConnectManagementThread implements Runnable, OpenVPNManagement {
    public static final int STATE_AUTHENTICATED = 3;
    public static final int STATE_AUTHENTICATING = 1;
    public static final int STATE_CONNECTED = 5;
    public static final int STATE_CONNECTING = 4;
    public static final int STATE_DISCONNECTED = 6;
    public static final int STATE_USER_PROMPT = 2;
    public static final String TAG = "OpenConnect";
    private SharedPreferences mAppPrefs;
    private String mCacheDir;
    private Context mContext;
    private String mFilesDir;
    private String mLastFormDigest;
    private LibOpenConnect mOC;
    private OpenVpnService mOpenVPNService;
    private SharedPreferences mPrefs;
    private VpnProfile mProfile;
    private boolean mRequestDisconnect;
    private boolean mRequestPause;
    private String mServerAddr;
    private boolean mAuthgroupSet = false;
    private HashMap<String, Boolean> mAcceptedCerts = new HashMap<>();
    private HashMap<String, Boolean> mRejectedCerts = new HashMap<>();
    private boolean mAuthDone = false;
    private Object mMainloopLock = new Object();

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/core/OpenConnectManagementThread$AndroidOC.class */
    public class AndroidOC extends LibOpenConnect {
        public final OpenConnectManagementThread this$0;

        private AndroidOC(OpenConnectManagementThread openConnectManagementThread) {
            this.this$0 = openConnectManagementThread;
        }

        private String getPeerCertSHA1() {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
                messageDigest.reset();
                messageDigest.update(getPeerCertDER());
                Formatter formatter = new Formatter();
                for (byte b8 : messageDigest.digest()) {
                    formatter.format("%02X", Byte.valueOf(b8));
                }
                String formatter2 = formatter.toString();
                formatter.close();
                return formatter2;
            } catch (Exception e8) {
                this.this$0.log("getPeerCertSHA1: could not initialize MessageDigest");
                return null;
            }
        }

        @Override // org.infradead.libopenconnect.LibOpenConnect
        public int onProcessAuthForm(LibOpenConnect.AuthForm authForm) {
            this.this$0.log("CALLBACK: onProcessAuthForm");
            if (authForm.error != null) {
                OpenConnectManagementThread openConnectManagementThread = this.this$0;
                StringBuilder r7 = androidx.activity.result.a.r("AUTH: error '");
                r7.append(authForm.error);
                r7.append("'");
                openConnectManagementThread.log(r7.toString());
            }
            if (authForm.message != null) {
                OpenConnectManagementThread openConnectManagementThread2 = this.this$0;
                StringBuilder r8 = androidx.activity.result.a.r("AUTH: message '");
                r8.append(authForm.message);
                r8.append("'");
                openConnectManagementThread2.log(r8.toString());
            }
            this.this$0.setState(2);
            AuthFormHandler authFormHandler = new AuthFormHandler(this.this$0.mPrefs, authForm, this.this$0.mAuthgroupSet, this.this$0.mLastFormDigest);
            authFormHandler.setCredentials(new Credentials(e.b(this.this$0.mContext, "USERNAME", BuildConfig.FLAVOR), e.b(this.this$0.mContext, "PASSWORD", BuildConfig.FLAVOR)));
            Integer num = (Integer) this.this$0.mOpenVPNService.promptUser(authFormHandler);
            if (num.intValue() == 0) {
                this.this$0.setState(1);
                this.this$0.mLastFormDigest = authFormHandler.getFormDigest();
            } else if (num.intValue() == 2) {
                OpenConnectManagementThread openConnectManagementThread3 = this.this$0;
                StringBuilder r9 = androidx.activity.result.a.r("AUTH: requesting authgroup change ");
                r9.append(this.this$0.mAuthgroupSet ? "(interactive)" : "(non-interactive)");
                openConnectManagementThread3.log(r9.toString());
                this.this$0.mAuthgroupSet = true;
            } else {
                this.this$0.log("AUTH: form result is " + num);
            }
            return num.intValue();
        }

        @Override // org.infradead.libopenconnect.LibOpenConnect
        public void onProgress(int i7, String str) {
            Log.d("OpenConnectStaus", str);
            OpenVpnService openVpnService = this.this$0.mOpenVPNService;
            StringBuilder r7 = androidx.activity.result.a.r("LIB: ");
            r7.append(str.trim());
            openVpnService.log(i7, r7.toString());
        }

        @Override // org.infradead.libopenconnect.LibOpenConnect
        public void onProtectSocket(int i7) {
            if (!this.this$0.mOpenVPNService.protect(i7)) {
                this.this$0.log("Error protecting fd " + i7);
            }
        }

        @Override // org.infradead.libopenconnect.LibOpenConnect
        public void onStatsUpdate(LibOpenConnect.VPNStats vPNStats) {
            this.this$0.mOpenVPNService.setStats(vPNStats);
        }

        @Override // org.infradead.libopenconnect.LibOpenConnect
        public int onValidatePeerCert(String str) {
            this.this$0.log("CALLBACK: onValidatePeerCert");
            String lowerCase = getPeerCertSHA1().toLowerCase(Locale.US);
            if (this.this$0.isCertAccepted(lowerCase)) {
                return 0;
            }
            if (this.this$0.mRejectedCerts.containsKey(lowerCase)) {
                return -1;
            }
            if (this.this$0.mAuthDone) {
                this.this$0.log("AUTH: certificate mismatch on existing connection");
                return -1;
            }
            Integer num = 2;
            if (num.intValue() != 0) {
                this.this$0.acceptCert(lowerCase, num.intValue() == 2);
                return 0;
            }
            this.this$0.log("AUTH: user rejected bad certificate");
            this.this$0.mRejectedCerts.put(lowerCase, Boolean.TRUE);
            return -1;
        }

        @Override // org.infradead.libopenconnect.LibOpenConnect
        public int onWriteNewConfig(byte[] bArr) {
            this.this$0.log("CALLBACK: onWriteNewConfig");
            return 0;
        }
    }

    public OpenConnectManagementThread(Context context, VpnProfile vpnProfile, OpenVpnService openVpnService) {
        this.mContext = context;
        this.mProfile = vpnProfile;
        this.mOpenVPNService = openVpnService;
        this.mPrefs = vpnProfile.mPrefs;
        this.mAppPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acceptCert(String str, boolean z7) {
        this.mAcceptedCerts.put(str, Boolean.TRUE);
        if (z7) {
            putStringPref(androidx.activity.result.a.m("ACCEPTED-CERT-", str), "true");
        }
    }

    private void addDefaultRoutes(VpnService.Builder builder, LibOpenConnect.IPInfo iPInfo, ArrayList<String> arrayList) {
        Iterator<String> it = arrayList.iterator();
        boolean z7 = true;
        boolean z8 = true;
        while (it.hasNext()) {
            if (it.next().contains(":")) {
                z8 = false;
            } else {
                z7 = false;
            }
        }
        if (z7 && iPInfo.addr != null) {
            builder.addRoute("0.0.0.0", 0);
            log("ROUTE: 0.0.0.0/0");
        }
        if (!z8 || iPInfo.netmask6 == null) {
            return;
        }
        builder.addRoute("::", 0);
        log("ROUTE: ::/0");
    }

    private void addSubnetRoutes(VpnService.Builder builder, LibOpenConnect.IPInfo iPInfo, ArrayList<String> arrayList) {
        CIDRIP cidrip;
        String str;
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            String next = it.next();
            try {
                if (next.contains(":")) {
                    String[] split = next.split("/");
                    if (split.length == 1) {
                        builder.addRoute(split[0], 128);
                    } else {
                        builder.addRoute(split[0], Integer.parseInt(split[1]));
                    }
                    str = "ROUTE: " + next;
                } else {
                    if (next.contains("/")) {
                        cidrip = new CIDRIP(next);
                    } else {
                        cidrip = new CIDRIP(next + "/32");
                    }
                    builder.addRoute(cidrip.mIp, cidrip.len);
                    str = "ROUTE: " + cidrip.mIp + "/" + cidrip.len;
                }
                log(str);
            } catch (Exception e8) {
                log(androidx.activity.result.a.n("ROUTE: skipping invalid route '", next, "'"));
            }
        }
    }

    private byte[] decodeBase64(String str) {
        if (str.matches("^[A-Za-z0-9+/=\\n]+$")) {
            return Base64.decode(str, 0);
        }
        throw new IllegalArgumentException("invalid chars");
    }

    private void errorAlert() {
        errorAlert(this.mContext.getString(2131755264, this.mOC.getHostname()));
    }

    private void errorAlert(String str) {
        this.mOpenVPNService.promptUser(new ErrorDialog(this.mPrefs, this.mContext.getString(2131755266), str));
    }

    private void extractBinaries() {
        if (!AssetExtractor.extractAll(this.mContext)) {
            log("Error extracting assets");
        }
        try {
            String str = this.mFilesDir + "/curl-bin";
            StringBuilder sb = new StringBuilder();
            sb.append(this.mFilesDir);
            sb.append("/run_pie ");
            writeCertOrScript(this.mFilesDir + "/curl", "#!/system/bin/sh\nexec " + BuildConfig.FLAVOR + str + " \"$@\"\n", true);
        } catch (IOException e8) {
            log("Error writing curl wrapper scripts");
        }
    }

    private String formatTime(long j7) {
        return j7 <= 0 ? "NEVER" : DateFormat.getDateTimeInstance(3, 3, Locale.US).format(Long.valueOf(j7));
    }

    private boolean getBoolPref(String str) {
        return this.mPrefs.getBoolean(str, false);
    }

    private String getStringPref(String str) {
        return this.mPrefs.getString(str, BuildConfig.FLAVOR);
    }

    private boolean getSubnetPref(ArrayList<String> arrayList) {
        for (String str : getStringPref("split_tunnel_networks").split("[,\\s]+")) {
            if (!str.equals(BuildConfig.FLAVOR)) {
                arrayList.add(str);
            }
        }
        if (!arrayList.isEmpty()) {
            return true;
        }
        log("ROUTE: split tunnel list is empty; check your VPN settings");
        return false;
    }

    private int inlineToTempFile(String str, String str2, boolean z7) {
        int writeCertOrScript;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(str);
            byte[] decodeBase64 = decodeBase64(str2);
            int length = decodeBase64.length;
            if (z7) {
                try {
                    if (rewriteShell(new String(decodeBase64))) {
                        fileOutputStream.write("#!/system/bin/sh\n".getBytes());
                    }
                } catch (Exception e8) {
                }
            }
            fileOutputStream.write(decodeBase64);
            fileOutputStream.close();
            writeCertOrScript = length;
            if (z7) {
                setExecutable(str);
                writeCertOrScript = length;
            }
        } catch (IOException e9) {
            return -1;
        } catch (IllegalArgumentException e10) {
            writeCertOrScript = writeCertOrScript(str, str2, z7);
        }
        return writeCertOrScript;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCertAccepted(String str) {
        return this.mAcceptedCerts.containsKey(str) || getStringPref(androidx.activity.result.a.m("ACCEPTED-CERT-", str)).equals("true");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        this.mOpenVPNService.log(1, str);
    }

    private void logOneStat(String str) {
        log("STAT: " + str + "=" + this.mPrefs.getLong(str, 0L) + "; first=" + formatTime(this.mPrefs.getLong(str + "_first", 0L)) + "; prev=" + formatTime(this.mPrefs.getLong(str + "_prev", 0L)));
    }

    private void logStats() {
        logOneStat("attempt");
        logOneStat("connect");
        logOneStat("cancel");
    }

    private String prefToTempFile(String str, boolean z7) {
        String str2;
        int writeCertOrScript;
        StringBuilder sb;
        StringBuilder sb2;
        String stringPref = getStringPref(str);
        String str3 = this.mCacheDir + File.separator + str + ".tmp";
        if (stringPref.equals(BuildConfig.FLAVOR)) {
            return null;
        }
        if (stringPref.startsWith(VpnProfile.INLINE_TAG)) {
            writeCertOrScript = inlineToTempFile(str3, stringPref.substring(10), z7);
            if (writeCertOrScript < 0) {
                sb2 = new StringBuilder();
                log(g.c(sb2, "PREF: I/O exception writing ", str));
                return null;
            }
            sb = new StringBuilder();
            sb.append("PREF: wrote out ");
            sb.append(str3);
            sb.append(" (");
            sb.append(writeCertOrScript);
            sb.append(")");
            log(sb.toString());
            str2 = str3;
        } else {
            log(androidx.activity.result.a.m("PREF: using existing file ", stringPref));
            if (!stringPref.startsWith("/")) {
                stringPref = ProfileManager.getCertPath() + stringPref;
            }
            if (z7) {
                String readStringFromFile = AssetExtractor.readStringFromFile(stringPref);
                if (readStringFromFile == null) {
                    return null;
                }
                writeCertOrScript = writeCertOrScript(str3, readStringFromFile, true);
                if (writeCertOrScript < 0) {
                    sb2 = new StringBuilder();
                    log(g.c(sb2, "PREF: I/O exception writing ", str));
                    return null;
                }
                sb = new StringBuilder();
                sb.append("PREF: wrote out ");
                sb.append(str3);
                sb.append(" (");
                sb.append(writeCertOrScript);
                sb.append(")");
                log(sb.toString());
                str2 = str3;
            } else {
                str2 = stringPref;
            }
        }
        return str2;
    }

    private void putStringPref(String str, String str2) {
        this.mPrefs.edit().putString(str, str2).commit();
    }

    private boolean rewriteShell(String str) {
        Matcher matcher = Pattern.compile("^#![ \\t]*(/\\S+)[ \\t\\n]").matcher(str);
        return matcher.find() && !new File(matcher.group(1)).exists();
    }

    private boolean runVPN() {
        updateStatPref("attempt");
        this.mFilesDir = this.mContext.getFilesDir().getPath();
        this.mCacheDir = this.mContext.getCacheDir().getPath();
        extractBinaries();
        setState(4);
        synchronized (this.mMainloopLock) {
            this.mOC = new AndroidOC();
        }
        if (!setPreferences()) {
            return false;
        }
        if (this.mOC.parseURL(this.mServerAddr) != 0) {
            log("Error parsing server address");
            errorAlert(this.mContext.getString(2131755272, this.mServerAddr));
            return false;
        }
        int obtainCookie = this.mOC.obtainCookie();
        if (obtainCookie < 0) {
            if (!this.mRejectedCerts.isEmpty() || this.mRequestDisconnect) {
                updateStatPref("cancel");
                return false;
            }
            log("Error obtaining cookie");
            errorAlert();
            return false;
        }
        if (obtainCookie > 0) {
            log("User canceled auth dialog");
            updateStatPref("cancel");
            return false;
        }
        this.mAuthDone = true;
        UserDialog.writeDeferredPrefs();
        setState(3);
        if (this.mOC.makeCSTPConnection() != 0) {
            if (this.mRequestDisconnect) {
                return false;
            }
            log("Error establishing CSTP connection");
            errorAlert();
            return false;
        }
        VpnService.Builder vpnServiceBuilder = this.mOpenVPNService.getVpnServiceBuilder();
        d.a(this.mContext, vpnServiceBuilder);
        setIPInfo(vpnServiceBuilder);
        try {
            ParcelFileDescriptor establish = vpnServiceBuilder.establish();
            w2.e eVar = w2.e.f6502a;
            w2.e.a();
            if (establish == null || this.mOC.setupTunFD(establish.getFd()) != 0) {
                log("Error setting up tunnel fd");
                errorAlert();
                return false;
            }
            setState(5);
            updateStatPref("connect");
            this.mOC.setupDTLS(60);
            while (this.mOC.mainloop(TSIG.FUDGE, 10) >= 0) {
                synchronized (this.mMainloopLock) {
                    if (!this.mRequestDisconnect) {
                        while (this.mRequestPause) {
                            try {
                                this.mMainloopLock.wait();
                            } catch (InterruptedException e8) {
                            }
                        }
                    }
                }
            }
            try {
                establish.close();
                return true;
            } catch (IOException e9) {
                return true;
            }
        } catch (Exception e10) {
            StringBuilder r7 = androidx.activity.result.a.r("Exception during establish(): ");
            r7.append(e10.getLocalizedMessage());
            log(r7.toString());
            return false;
        }
    }

    private boolean setExecutable(String str) {
        File file = new File(str);
        if (!file.exists()) {
            log("PREF: file does not exist");
            return false;
        }
        if (file.setExecutable(true)) {
            return true;
        }
        throw new IOException();
    }

    private void setIPInfo(VpnService.Builder builder) {
        String str;
        String str2;
        LibOpenConnect.IPInfo iPInfo = this.mOC.getIPInfo();
        String str3 = iPInfo.addr;
        if (str3 != null && (str2 = iPInfo.netmask) != null) {
            CIDRIP cidrip = new CIDRIP(str3, str2);
            builder.addAddress(cidrip.mIp, cidrip.len);
            log("IPv4: " + cidrip.mIp + "/" + cidrip.len);
        }
        String str4 = iPInfo.netmask6;
        if (str4 != null) {
            String[] split = str4.split("/");
            if (split.length == 2) {
                builder.addAddress(split[0], Integer.parseInt(split[1]));
                log("IPv6: " + iPInfo.netmask6);
            }
        }
        int i7 = iPInfo.MTU;
        if (i7 < 1280) {
            builder.setMtu(1280);
            str = "MTU: 1280 (forced)";
        } else {
            builder.setMtu(i7);
            str = "MTU: " + iPInfo.MTU;
        }
        log(str);
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> arrayList2 = iPInfo.DNS;
        String str5 = iPInfo.domain;
        if (getStringPref("split_tunnel_mode").equals("on_vpn_dns")) {
            getSubnetPref(arrayList);
        } else if (getStringPref("split_tunnel_mode").equals("on_uplink_dns")) {
            getSubnetPref(arrayList);
            arrayList2 = new ArrayList<>();
            str5 = null;
        } else {
            arrayList = iPInfo.splitIncludes;
            addDefaultRoutes(builder, iPInfo, arrayList);
        }
        addSubnetRoutes(builder, iPInfo, arrayList);
        Iterator<String> it = arrayList2.iterator();
        while (it.hasNext()) {
            String next = it.next();
            builder.addDnsServer(next);
            builder.addRoute(next, next.contains(":") ? 128 : 32);
            log("DNS: " + next);
        }
        if (str5 != null) {
            builder.addSearchDomain(str5);
            log("DOMAIN: " + str5);
        }
        this.mOpenVPNService.setIPInfo(iPInfo, this.mOC.getHostname());
    }

    private boolean setPreferences() {
        String str;
        int tokenMode;
        try {
            String str2 = System.getenv("PATH");
            String str3 = str2;
            if (!str2.startsWith(this.mFilesDir)) {
                str3 = this.mFilesDir + ":" + str2;
            }
            String prefToTempFile = prefToTempFile("custom_csd_wrapper", true);
            LibOpenConnect libOpenConnect = this.mOC;
            if (prefToTempFile == null) {
                prefToTempFile = this.mFilesDir + File.separator + "android_csd.sh";
            }
            libOpenConnect.setCSDWrapper(prefToTempFile, this.mCacheDir, str3);
            String prefToTempFile2 = prefToTempFile("ca_certificate", false);
            if (prefToTempFile2 != null) {
                this.mOC.setCAFile(prefToTempFile2);
            }
            String prefToTempFile3 = prefToTempFile(VpnProfileDataSource.KEY_USER_CERTIFICATE, false);
            String prefToTempFile4 = prefToTempFile("private_key", false);
            if (prefToTempFile3 != null) {
                if (prefToTempFile4 == null) {
                    this.mOC.setClientCert(prefToTempFile3, prefToTempFile3);
                } else {
                    this.mOC.setClientCert(prefToTempFile3, prefToTempFile4);
                }
            }
            this.mServerAddr = getStringPref("server_address");
            this.mOC.setXMLPost(i.I.optBoolean("DisableXMLPost", false));
            this.mOC.setPFS(i.I.optBoolean("RequirePFS", true));
            String stringPref = getStringPref("reported_os");
            this.mOC.setReportedOS(stringPref);
            if (stringPref.equals("android") || stringPref.equals("apple-ios")) {
                this.mOC.setMobileInfo("1.0", stringPref, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            }
            if (getBoolPref("dpd_override")) {
                try {
                    int parseInt = Integer.parseInt(getStringPref("dpd_value"));
                    if (parseInt > 0) {
                        this.mOC.setDPD(parseInt);
                    }
                } catch (Exception e8) {
                    log("DPD: bad dpd_value, ignoring");
                }
            }
            String stringPref2 = getStringPref("software_token");
            String stringPref3 = getStringPref("token_string");
            tokenMode = stringPref2.equals("securid") ? this.mOC.setTokenMode(1, stringPref3) : stringPref2.equals("totp") ? this.mOC.setTokenMode(2, stringPref3) : 0;
        } catch (IOException e9) {
            str = "Error writing temporary file";
        }
        if (tokenMode >= 0) {
            prefChanged();
            return true;
        }
        str = "Error " + tokenMode + " setting token string";
        log(str);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setState(int i7) {
        synchronized (this) {
            this.mOpenVPNService.setConnectionState(i7);
        }
    }

    private void updateLogLevel() {
        LibOpenConnect libOpenConnect;
        int i7;
        if (this.mAppPrefs.getBoolean("trace_log", false)) {
            libOpenConnect = this.mOC;
            i7 = 3;
        } else {
            libOpenConnect = this.mOC;
            i7 = 2;
        }
        libOpenConnect.setLogLevel(i7);
    }

    private void updateStatPref(String str) {
        long j7 = this.mPrefs.getLong(str, 0L);
        long currentTimeMillis = System.currentTimeMillis();
        long j8 = this.mPrefs.getLong(str + "_first", currentTimeMillis);
        SharedPreferences.Editor edit = this.mPrefs.edit();
        edit.putLong(str, j7 + 1);
        edit.putLong(str + "_first", j8);
        edit.putLong(g.c(new StringBuilder(), str, "_prev"), currentTimeMillis);
        edit.apply();
    }

    private int writeCertOrScript(String str, String str2, boolean z7) {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(str), "utf-8"));
        if (z7 && rewriteShell(str2)) {
            bufferedWriter.write("#!/system/bin/sh\n");
        }
        bufferedWriter.write(str2);
        bufferedWriter.close();
        if (z7) {
            setExecutable(str);
        }
        return str2.length();
    }

    @Override // app.openconnect.core.OpenVPNManagement
    public void pause() {
        LibOpenConnect libOpenConnect;
        log("PAUSE");
        synchronized (this.mMainloopLock) {
            if (!this.mRequestPause && !this.mRequestDisconnect && (libOpenConnect = this.mOC) != null) {
                this.mRequestPause = true;
                libOpenConnect.pause();
            }
        }
    }

    @Override // app.openconnect.core.OpenVPNManagement
    public void prefChanged() {
        updateLogLevel();
    }

    @Override // app.openconnect.core.OpenVPNManagement
    public void reconnect() {
        log("RECONNECT");
        synchronized (this.mMainloopLock) {
            LibOpenConnect libOpenConnect = this.mOC;
            if (libOpenConnect != null) {
                libOpenConnect.pause();
            }
        }
    }

    public void requestStats() {
        boolean z7;
        LibOpenConnect libOpenConnect;
        synchronized (this.mMainloopLock) {
            if (this.mRequestPause || this.mRequestDisconnect || (libOpenConnect = this.mOC) == null) {
                z7 = true;
            } else {
                libOpenConnect.requestStats();
                z7 = false;
            }
        }
        if (z7) {
            this.mOpenVPNService.setStats(null);
        }
    }

    @Override // app.openconnect.core.OpenVPNManagement
    public void resume() {
        log("RESUME");
        synchronized (this.mMainloopLock) {
            if (this.mRequestPause) {
                this.mRequestPause = false;
                this.mMainloopLock.notify();
            }
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        logStats();
        try {
            if (this.mAppPrefs.getBoolean("loadTunModule", false)) {
                c.c(new b("insmod /system/lib/modules/tun.ko"));
            }
            if (this.mAppPrefs.getBoolean("useCM9Fix", false)) {
                c.c(new b("chown 1000 /dev/tun"));
            }
        } catch (Exception e8) {
            StringBuilder r7 = androidx.activity.result.a.r("error running root commands: ");
            r7.append(e8.getLocalizedMessage());
            log(r7.toString());
        }
        if (!runVPN()) {
            log("VPN terminated with errors");
        }
        setState(6);
        synchronized (this.mMainloopLock) {
            this.mOC.destroy();
            this.mOC = null;
        }
        UserDialog.clearDeferredPrefs();
        this.mOpenVPNService.threadDone();
    }

    @Override // app.openconnect.core.OpenVPNManagement
    public boolean stopVPN() {
        LibOpenConnect libOpenConnect;
        log("STOP");
        synchronized (this.mMainloopLock) {
            if (this.mRequestDisconnect || (libOpenConnect = this.mOC) == null) {
                return true;
            }
            this.mRequestDisconnect = true;
            this.mRequestPause = false;
            libOpenConnect.cancel();
            this.mMainloopLock.notify();
            return true;
        }
    }
}
