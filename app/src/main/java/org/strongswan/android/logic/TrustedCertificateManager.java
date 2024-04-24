package org.strongswan.android.logic;

import android.util.Log;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/TrustedCertificateManager.class */
public class TrustedCertificateManager extends Observable {
    private static final String TAG = "TrustedCertificateManager";
    private Hashtable<String, X509Certificate> mCACerts;
    private final ArrayList<KeyStore> mKeyStores;
    private boolean mLoaded;
    private final ReentrantReadWriteLock mLock;
    private volatile boolean mReload;

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/TrustedCertificateManager$Singleton.class */
    public static class Singleton {
        public static final TrustedCertificateManager mInstance = new TrustedCertificateManager();

        private Singleton() {
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/TrustedCertificateManager$TrustedCertificateSource.class */
    public enum TrustedCertificateSource {
        SYSTEM("system:"),
        USER("user:"),
        LOCAL("local:");

        private final String mPrefix;

        TrustedCertificateSource(String str) {
            this.mPrefix = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getPrefix() {
            return this.mPrefix;
        }
    }

    private TrustedCertificateManager() {
        this.mLock = new ReentrantReadWriteLock();
        this.mCACerts = new Hashtable<>();
        this.mKeyStores = new ArrayList<>();
        for (int i7 = 0; i7 < 2; i7++) {
            String str = new String[]{"LocalCertificateStore", "AndroidCAStore"}[i7];
            try {
                KeyStore keyStore = KeyStore.getInstance(str);
                keyStore.load(null, null);
                this.mKeyStores.add(keyStore);
            } catch (Exception e8) {
                Log.e(TAG, "Unable to load KeyStore: " + str);
                e8.printStackTrace();
            }
        }
    }

    private void fetchCertificates(Hashtable<String, X509Certificate> hashtable, KeyStore keyStore) {
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String nextElement = aliases.nextElement();
                Certificate certificate = keyStore.getCertificate(nextElement);
                if (certificate != null && (certificate instanceof X509Certificate)) {
                    hashtable.put(nextElement, (X509Certificate) certificate);
                }
            }
        } catch (KeyStoreException e8) {
            e8.printStackTrace();
        }
    }

    public static TrustedCertificateManager getInstance() {
        return Singleton.mInstance;
    }

    private void loadCertificates() {
        Log.d(TAG, "Load cached CA certificates");
        Hashtable<String, X509Certificate> hashtable = new Hashtable<>();
        Iterator<KeyStore> it = this.mKeyStores.iterator();
        while (it.hasNext()) {
            fetchCertificates(hashtable, it.next());
        }
        this.mCACerts = hashtable;
        if (!this.mLoaded) {
            setChanged();
            notifyObservers();
            this.mLoaded = true;
        }
        Log.d(TAG, "Cached CA certificates loaded");
    }

    public Hashtable<String, X509Certificate> getAllCACertificates() {
        this.mLock.readLock().lock();
        Hashtable<String, X509Certificate> hashtable = (Hashtable) this.mCACerts.clone();
        this.mLock.readLock().unlock();
        return hashtable;
    }

    public X509Certificate getCACertificateFromAlias(String str) {
        X509Certificate x509Certificate;
        if (!this.mLock.readLock().tryLock()) {
            Iterator<KeyStore> it = this.mKeyStores.iterator();
            while (true) {
                if (!it.hasNext()) {
                    x509Certificate = null;
                    break;
                }
                try {
                    Certificate certificate = it.next().getCertificate(str);
                    if (certificate != null && (certificate instanceof X509Certificate)) {
                        x509Certificate = (X509Certificate) certificate;
                        break;
                    }
                } catch (KeyStoreException e8) {
                    e8.printStackTrace();
                }
            }
        } else {
            x509Certificate = this.mCACerts.get(str);
            this.mLock.readLock().unlock();
        }
        return x509Certificate;
    }

    public Hashtable<String, X509Certificate> getCACertificates(TrustedCertificateSource trustedCertificateSource) {
        Hashtable<String, X509Certificate> hashtable = new Hashtable<>();
        this.mLock.readLock().lock();
        for (String str : this.mCACerts.keySet()) {
            if (str.startsWith(trustedCertificateSource.getPrefix())) {
                hashtable.put(str, this.mCACerts.get(str));
            }
        }
        this.mLock.readLock().unlock();
        return hashtable;
    }

    public TrustedCertificateManager load() {
        Log.d(TAG, "Ensure cached CA certificates are loaded");
        this.mLock.writeLock().lock();
        if (!this.mLoaded || this.mReload) {
            this.mReload = false;
            loadCertificates();
        }
        this.mLock.writeLock().unlock();
        return this;
    }

    public TrustedCertificateManager reset() {
        Log.d(TAG, "Force reload of cached CA certificates on next load");
        this.mReload = true;
        setChanged();
        notifyObservers();
        return this;
    }
}
