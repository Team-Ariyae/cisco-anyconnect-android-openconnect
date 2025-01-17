package org.infradead.libopenconnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: TehShop-dex2jar.jar:org/infradead/libopenconnect/LibOpenConnect.class */
public abstract class LibOpenConnect {
    public static final int OC_FORM_OPT_HIDDEN = 4;
    public static final int OC_FORM_OPT_IGNORE = 1;
    public static final int OC_FORM_OPT_NUMERIC = 2;
    public static final int OC_FORM_OPT_PASSWORD = 2;
    public static final int OC_FORM_OPT_SELECT = 3;
    public static final int OC_FORM_OPT_TEXT = 1;
    public static final int OC_FORM_OPT_TOKEN = 5;
    public static final int OC_FORM_RESULT_CANCELLED = 1;
    public static final int OC_FORM_RESULT_ERR = -1;
    public static final int OC_FORM_RESULT_NEWGROUP = 2;
    public static final int OC_FORM_RESULT_OK = 0;
    public static final int OC_TOKEN_MODE_HOTP = 3;
    public static final int OC_TOKEN_MODE_NONE = 0;
    public static final int OC_TOKEN_MODE_STOKEN = 1;
    public static final int OC_TOKEN_MODE_TOTP = 2;
    public static final int PRG_DEBUG = 2;
    public static final int PRG_ERR = 0;
    public static final int PRG_INFO = 1;
    public static final int PRG_TRACE = 3;
    public static final int RECONNECT_INTERVAL_MAX = 100;
    public static final int RECONNECT_INTERVAL_MIN = 10;
    public Object userData;
    public boolean canceled = false;
    public Object asyncLock = new Object();
    public long libctx = init("OpenConnect VPN Agent (Java)");

    /* loaded from: TehShop-dex2jar.jar:org/infradead/libopenconnect/LibOpenConnect$AuthForm.class */
    public static class AuthForm {
        public String action;
        public String authID;
        public FormOpt authgroupOpt;
        public int authgroupSelection;
        public String banner;
        public String error;
        public String message;
        public String method;
        public ArrayList<FormOpt> opts = new ArrayList<>();
        public Object userData;

        public FormOpt addOpt(boolean z7) {
            FormOpt formOpt = new FormOpt();
            this.opts.add(formOpt);
            if (z7) {
                this.authgroupOpt = formOpt;
            }
            return formOpt;
        }

        public String getOptValue(String str) {
            Iterator<FormOpt> it = this.opts.iterator();
            while (it.hasNext()) {
                FormOpt next = it.next();
                if (next.name.equals(str)) {
                    return next.value;
                }
            }
            return null;
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/infradead/libopenconnect/LibOpenConnect$FormChoice.class */
    public static class FormChoice {
        public String authType;
        public String label;
        public String name;
        public String overrideLabel;
        public String overrideName;
        public Object userData;
    }

    /* loaded from: TehShop-dex2jar.jar:org/infradead/libopenconnect/LibOpenConnect$FormOpt.class */
    public static class FormOpt {
        public ArrayList<FormChoice> choices = new ArrayList<>();
        public long flags;
        public String label;
        public String name;
        public int type;
        public Object userData;
        public String value;

        public void addChoice(FormChoice formChoice) {
            this.choices.add(formChoice);
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/infradead/libopenconnect/LibOpenConnect$IPInfo.class */
    public static class IPInfo {
        public int MTU;
        public String addr;
        public String addr6;
        public String domain;
        public String gatewayAddr;
        public String netmask;
        public String netmask6;
        public String proxyPac;
        public Object userData;
        public ArrayList<String> DNS = new ArrayList<>();
        public ArrayList<String> NBNS = new ArrayList<>();
        public ArrayList<String> splitDNS = new ArrayList<>();
        public ArrayList<String> splitIncludes = new ArrayList<>();
        public ArrayList<String> splitExcludes = new ArrayList<>();
        public HashMap<String, String> CSTPOptions = new HashMap<>();
        public HashMap<String, String> DTLSOptions = new HashMap<>();

        public void addCSTPOption(String str, String str2) {
            this.CSTPOptions.put(str, str2);
        }

        public void addDNS(String str) {
            this.DNS.add(str);
        }

        public void addDTLSOption(String str, String str2) {
            this.DTLSOptions.put(str, str2);
        }

        public void addNBNS(String str) {
            this.NBNS.add(str);
        }

        public void addSplitDNS(String str) {
            this.splitDNS.add(str);
        }

        public void addSplitExclude(String str) {
            this.splitExcludes.add(str);
        }

        public void addSplitInclude(String str) {
            this.splitIncludes.add(str);
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/infradead/libopenconnect/LibOpenConnect$VPNStats.class */
    public static class VPNStats {
        public long rxBytes;
        public long rxPkts;
        public long txBytes;
        public long txPkts;
        public Object userData;
    }

    static {
        globalInit();
    }

    public static native String getVersion();

    public static native synchronized void globalInit();

    public static native boolean hasOATHSupport();

    public static native boolean hasPKCS11Support();

    public static native boolean hasStokenSupport();

    public static native boolean hasTSSBlobSupport();

    public static native boolean hasYubiOATHSupport();

    public void cancel() {
        synchronized (this.asyncLock) {
            if (!this.canceled) {
                doCancel();
                this.canceled = true;
            }
        }
    }

    public native synchronized int checkPeerCertHash(String str);

    public native synchronized void clearCookie();

    public void destroy() {
        synchronized (this) {
            if (this.libctx != 0) {
                free();
                this.libctx = 0L;
            }
        }
    }

    public native void doCancel();

    public native synchronized void free();

    public native synchronized String getCSTPCipher();

    public native synchronized String getCSTPCompression();

    public native synchronized String getCookie();

    public native synchronized String getDNSName();

    public native synchronized String getDTLSCipher();

    public native synchronized String getDTLSCompression();

    public native synchronized String getHostname();

    public native synchronized String getIFName();

    public native synchronized IPInfo getIPInfo();

    public native synchronized byte[][] getPeerCertChain();

    public native synchronized byte[] getPeerCertDER();

    public native synchronized String getPeerCertDetails();

    public native synchronized String getPeerCertHash();

    public native synchronized int getPort();

    public native synchronized String getUrlpath();

    public native synchronized long init(String str);

    public boolean isCanceled() {
        boolean z7;
        synchronized (this.asyncLock) {
            z7 = this.canceled;
        }
        return z7;
    }

    public native synchronized int mainloop(int i7, int i8);

    public native synchronized int makeCSTPConnection();

    public native synchronized int obtainCookie();

    public abstract int onProcessAuthForm(AuthForm authForm);

    public abstract void onProgress(int i7, String str);

    public void onProtectSocket(int i7) {
    }

    public void onReconnected() {
    }

    public void onSetupTun() {
    }

    public void onStatsUpdate(VPNStats vPNStats) {
    }

    public int onTokenLock() {
        return 0;
    }

    public int onTokenUnlock(String str) {
        return 0;
    }

    public int onValidatePeerCert(String str) {
        return 0;
    }

    public int onWriteNewConfig(byte[] bArr) {
        return 0;
    }

    public native synchronized int parseURL(String str);

    public native synchronized int passphraseFromFSID();

    public native void pause();

    public native void requestStats();

    public native synchronized void resetSSL();

    public native synchronized void setCAFile(String str);

    public native synchronized void setCSDWrapper(String str, String str2, String str3);

    public native synchronized void setCertExpiryWarning(int i7);

    public native synchronized void setClientCert(String str, String str2);

    public native synchronized void setDPD(int i7);

    public native synchronized int setHTTPProxy(String str);

    public native synchronized void setHostname(String str);

    public native synchronized void setLocalName(String str);

    public native void setLogLevel(int i7);

    public native synchronized void setMobileInfo(String str, String str2, String str3);

    public native synchronized void setPFS(boolean z7);

    public native synchronized int setProxyAuth(String str);

    public native synchronized void setReportedOS(String str);

    public native synchronized void setReqMTU(int i7);

    public native synchronized void setSystemTrust(boolean z7);

    public native synchronized int setTokenMode(int i7, String str);

    public native synchronized void setUrlpath(String str);

    public native synchronized void setXMLPost(boolean z7);

    public native synchronized void setXMLSHA1(String str);

    public native synchronized int setupDTLS(int i7);

    public native synchronized int setupTunDevice(String str, String str2);

    public native synchronized int setupTunFD(int i7);

    public native synchronized int setupTunScript(String str);
}
