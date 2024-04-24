package org.strongswan.android.security;

import androidx.activity.result.a;
import com.tehvpn.TehVPN;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import org.strongswan.android.utils.Utils;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/security/LocalCertificateStore.class */
public class LocalCertificateStore {
    private static final Pattern ALIAS_PATTERN = Pattern.compile("^local:[0-9a-f]{40}$");
    private static final String ALIAS_PREFIX = "local:";
    private static final String FILE_PREFIX = "certificate-";

    private String getKeyId(Certificate certificate) {
        try {
            return Utils.bytesToHex(MessageDigest.getInstance("SHA1").digest(certificate.getPublicKey().getEncoded()));
        } catch (NoSuchAlgorithmException e8) {
            e8.printStackTrace();
            return null;
        }
    }

    public boolean addCertificate(Certificate certificate) {
        String keyId;
        FileOutputStream openFileOutput;
        if (!(certificate instanceof X509Certificate) || (keyId = getKeyId(certificate)) == null) {
            return false;
        }
        try {
            try {
                openFileOutput = TehVPN.f2668d.openFileOutput(FILE_PREFIX + keyId, 0);
                try {
                    try {
                        openFileOutput.write(certificate.getEncoded());
                        try {
                            openFileOutput.close();
                            return true;
                        } catch (IOException e8) {
                            e8.printStackTrace();
                            return true;
                        }
                    } catch (IOException e9) {
                        e9.printStackTrace();
                        try {
                            openFileOutput.close();
                            return false;
                        } catch (IOException e10) {
                            e = e10;
                            e.printStackTrace();
                            return false;
                        }
                    }
                } catch (CertificateEncodingException e11) {
                    e11.printStackTrace();
                    try {
                        openFileOutput.close();
                        return false;
                    } catch (IOException e12) {
                        e = e12;
                        e.printStackTrace();
                        return false;
                    }
                }
            } catch (FileNotFoundException e13) {
                e13.printStackTrace();
                return false;
            }
        } catch (Throwable th) {
            try {
                openFileOutput.close();
            } catch (IOException e14) {
                e14.printStackTrace();
            }
            throw th;
        }
    }

    public ArrayList<String> aliases() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (String str : TehVPN.f2668d.fileList()) {
            if (str.startsWith(FILE_PREFIX)) {
                StringBuilder r7 = a.r(ALIAS_PREFIX);
                r7.append(str.substring(12));
                arrayList.add(r7.toString());
            }
        }
        return arrayList;
    }

    public boolean containsAlias(String str) {
        return getCreationDate(str) != null;
    }

    public void deleteCertificate(String str) {
        if (ALIAS_PATTERN.matcher(str).matches()) {
            String substring = str.substring(6);
            TehVPN.f2668d.deleteFile(FILE_PREFIX + substring);
        }
    }

    public X509Certificate getCertificate(String str) {
        if (!ALIAS_PATTERN.matcher(str).matches()) {
            return null;
        }
        String substring = str.substring(6);
        try {
            FileInputStream openFileInput = TehVPN.f2668d.openFileInput(FILE_PREFIX + substring);
            try {
                try {
                    X509Certificate x509Certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(openFileInput);
                    try {
                        openFileInput.close();
                    } catch (IOException e8) {
                        e8.printStackTrace();
                    }
                    return x509Certificate;
                } catch (CertificateException e9) {
                    e9.printStackTrace();
                    try {
                        openFileInput.close();
                        return null;
                    } catch (IOException e10) {
                        e10.printStackTrace();
                        return null;
                    }
                }
            } catch (Throwable th) {
                try {
                    openFileInput.close();
                } catch (IOException e11) {
                    e11.printStackTrace();
                }
                throw th;
            }
        } catch (FileNotFoundException e12) {
            e12.printStackTrace();
            return null;
        }
    }

    public String getCertificateAlias(Certificate certificate) {
        String keyId = getKeyId(certificate);
        return keyId != null ? a.m(ALIAS_PREFIX, keyId) : null;
    }

    public Date getCreationDate(String str) {
        if (!ALIAS_PATTERN.matcher(str).matches()) {
            return null;
        }
        String substring = str.substring(6);
        File fileStreamPath = TehVPN.f2668d.getFileStreamPath(FILE_PREFIX + substring);
        Date date = null;
        if (fileStreamPath.exists()) {
            date = new Date(fileStreamPath.lastModified());
        }
        return date;
    }
}
