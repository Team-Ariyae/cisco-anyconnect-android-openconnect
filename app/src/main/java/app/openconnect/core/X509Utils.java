package app.openconnect.core;

import android.content.Context;
import android.text.TextUtils;
import androidx.activity.result.a;
import app.openconnect.VpnProfile;
import d7.b;
import d7.c;
import io.github.inflationx.calligraphy3.BuildConfig;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.Character;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/X509Utils.class */
public class X509Utils {
    public static String getCertificateFriendlyName(Context context, String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                return getCertificateFriendlyName((X509Certificate) getCertificateFromFile(str));
            } catch (Exception e8) {
                StringBuilder r7 = a.r("Could not read certificate");
                r7.append(e8.getLocalizedMessage());
                OpenVPN.logError(r7.toString());
            }
        }
        return context.getString(2131755120);
    }

    public static String getCertificateFriendlyName(X509Certificate x509Certificate) {
        String[] split = x509Certificate.getSubjectX500Principal().getName().split(",");
        for (int i7 = 0; i7 < split.length; i7++) {
            String str = split[i7];
            if (str.startsWith("1.2.840.113549.1.9.1=#16")) {
                StringBuilder r7 = a.r("email=");
                r7.append(ia5decode(str.replace("1.2.840.113549.1.9.1=#16", BuildConfig.FLAVOR)));
                split[i7] = r7.toString();
            }
        }
        return TextUtils.join(",", split);
    }

    public static Certificate getCertificateFromFile(String str) {
        return CertificateFactory.getInstance("X.509").generateCertificate(str.startsWith(VpnProfile.INLINE_TAG) ? new ByteArrayInputStream(str.substring(Math.max(0, str.indexOf("-----BEGIN CERTIFICATE-----"))).getBytes()) : new FileInputStream(str));
    }

    private static String ia5decode(String str) {
        String n7;
        String str2 = BuildConfig.FLAVOR;
        int i7 = 1;
        while (i7 < str.length()) {
            String substring = str.substring(i7 - 1, i7 + 1);
            char parseInt = (char) Integer.parseInt(substring, 16);
            if (isPrintableChar(parseInt)) {
                n7 = str2 + parseInt;
            } else {
                if (i7 == 1) {
                    n7 = str2;
                    if (parseInt != 18) {
                        if (parseInt == 27) {
                            n7 = str2;
                        }
                    }
                }
                n7 = a.n(str2, "\\x", substring);
            }
            i7 += 2;
            str2 = n7;
        }
        return str2;
    }

    public static boolean isPrintableChar(char c) {
        Character.UnicodeBlock of = Character.UnicodeBlock.of(c);
        return (Character.isISOControl(c) || of == null || of == Character.UnicodeBlock.SPECIALS) ? false : true;
    }

    public static b readPemObjectFromFile(String str) {
        String readLine;
        b bVar;
        String readLine2;
        c cVar = new c(str.startsWith(VpnProfile.INLINE_TAG) ? new StringReader(str.replace(VpnProfile.INLINE_TAG, BuildConfig.FLAVOR)) : new FileReader(new File(str)));
        do {
            readLine = cVar.readLine();
            if (readLine == null) {
                break;
            }
        } while (!readLine.startsWith("-----BEGIN "));
        if (readLine != null) {
            String substring = readLine.substring(11);
            int indexOf = substring.indexOf(45);
            String substring2 = substring.substring(0, indexOf);
            if (indexOf > 0) {
                String m = a.m("-----END ", substring2);
                StringBuffer stringBuffer = new StringBuffer();
                ArrayList arrayList = new ArrayList();
                while (true) {
                    readLine2 = cVar.readLine();
                    if (readLine2 == null) {
                        break;
                    }
                    if (readLine2.indexOf(":") >= 0) {
                        int indexOf2 = readLine2.indexOf(58);
                        arrayList.add(new d7.a(readLine2.substring(0, indexOf2), readLine2.substring(indexOf2 + 1).trim()));
                    } else {
                        if (readLine2.indexOf(m) != -1) {
                            break;
                        }
                        stringBuffer.append(readLine2.trim());
                    }
                }
                if (readLine2 == null) {
                    throw new IOException(a.m(m, " not found"));
                }
                String stringBuffer2 = stringBuffer.toString();
                a7.b bVar2 = c7.a.f2116a;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((stringBuffer2.length() / 4) * 3);
                try {
                    c7.a.f2116a.a(stringBuffer2, byteArrayOutputStream);
                    byteArrayOutputStream.toByteArray();
                    bVar = new b(arrayList);
                    cVar.close();
                    return bVar;
                } catch (IOException e8) {
                    throw new RuntimeException("exception decoding base64 string: " + e8);
                }
            }
        }
        bVar = null;
        cVar.close();
        return bVar;
    }
}
