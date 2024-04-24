package org.strongswan.android.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/security/LocalCertificateKeyStoreSpi.class */
public class LocalCertificateKeyStoreSpi extends KeyStoreSpi {
    private final LocalCertificateStore mStore = new LocalCertificateStore();

    @Override // java.security.KeyStoreSpi
    public Enumeration<String> engineAliases() {
        return Collections.enumeration(this.mStore.aliases());
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineContainsAlias(String str) {
        return this.mStore.containsAlias(str);
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String str) {
        this.mStore.deleteCertificate(str);
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String str) {
        return this.mStore.getCertificate(str);
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate certificate) {
        return this.mStore.getCertificateAlias(certificate);
    }

    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String str) {
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String str) {
        return this.mStore.getCreationDate(str);
    }

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String str, char[] cArr) {
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String str) {
        return engineContainsAlias(str);
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String str) {
        return false;
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(InputStream inputStream, char[] cArr) {
        if (inputStream != null) {
            throw new UnsupportedOperationException();
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String str, Certificate certificate) {
        if (!this.mStore.addCertificate(certificate)) {
            throw new KeyStoreException();
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) {
        throw new UnsupportedOperationException();
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) {
        throw new UnsupportedOperationException();
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        return this.mStore.aliases().size();
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream outputStream, char[] cArr) {
        throw new UnsupportedOperationException();
    }
}
