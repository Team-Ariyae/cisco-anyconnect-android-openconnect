package org.strongswan.android.security;

import android.net.http.SslCertificate;
import io.github.inflationx.calligraphy3.BuildConfig;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/security/TrustedCertificateEntry.class */
public class TrustedCertificateEntry implements Comparable<TrustedCertificateEntry> {
    private final String mAlias;
    private final X509Certificate mCert;
    private String mString;
    private String mSubjectPrimary;
    private String mSubjectSecondary;

    public TrustedCertificateEntry(String str, X509Certificate x509Certificate) {
        this.mSubjectSecondary = BuildConfig.FLAVOR;
        this.mCert = x509Certificate;
        this.mAlias = str;
        try {
            SslCertificate sslCertificate = new SslCertificate(x509Certificate);
            String oName = sslCertificate.getIssuedTo().getOName();
            String uName = sslCertificate.getIssuedTo().getUName();
            String cName = sslCertificate.getIssuedTo().getCName();
            if (!oName.isEmpty()) {
                this.mSubjectPrimary = oName;
                if (!cName.isEmpty()) {
                    this.mSubjectSecondary = cName;
                } else if (!uName.isEmpty()) {
                    this.mSubjectSecondary = uName;
                }
            } else if (cName.isEmpty()) {
                this.mSubjectPrimary = sslCertificate.getIssuedTo().getDName();
            } else {
                this.mSubjectPrimary = cName;
            }
        } catch (NullPointerException e8) {
            this.mSubjectPrimary = x509Certificate.getSubjectDN().getName();
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(TrustedCertificateEntry trustedCertificateEntry) {
        int compareToIgnoreCase = this.mSubjectPrimary.compareToIgnoreCase(trustedCertificateEntry.mSubjectPrimary);
        int i7 = compareToIgnoreCase;
        if (compareToIgnoreCase == 0) {
            i7 = this.mSubjectSecondary.compareToIgnoreCase(trustedCertificateEntry.mSubjectSecondary);
        }
        return i7;
    }

    public String getAlias() {
        return this.mAlias;
    }

    public X509Certificate getCertificate() {
        return this.mCert;
    }

    public List<String> getSubjectAltNames() {
        ArrayList arrayList = new ArrayList();
        try {
            Collection<List<?>> subjectAlternativeNames = this.mCert.getSubjectAlternativeNames();
            if (subjectAlternativeNames != null) {
                for (List<?> list : subjectAlternativeNames) {
                    int intValue = ((Integer) list.get(0)).intValue();
                    if (intValue == 1 || intValue == 2 || intValue == 7) {
                        arrayList.add((String) list.get(1));
                    }
                }
            }
            Collections.sort(arrayList);
        } catch (CertificateParsingException e8) {
            e8.printStackTrace();
        }
        return arrayList;
    }

    public String getSubjectPrimary() {
        return this.mSubjectPrimary;
    }

    public String getSubjectSecondary() {
        return this.mSubjectSecondary;
    }

    public String toString() {
        if (this.mString == null) {
            this.mString = this.mSubjectPrimary;
            if (!this.mSubjectSecondary.isEmpty()) {
                this.mString += ", " + this.mSubjectSecondary;
            }
        }
        return this.mString;
    }
}
