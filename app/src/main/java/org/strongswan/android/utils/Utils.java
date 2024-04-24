package org.strongswan.android.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/utils/Utils.class */
public class Utils {
    public static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    public static String bytesToHex(byte[] bArr) {
        char[] cArr = new char[bArr.length * 2];
        for (int i7 = 0; i7 < bArr.length; i7++) {
            byte b8 = bArr[i7];
            int i8 = i7 * 2;
            char[] cArr2 = HEXDIGITS;
            cArr[i8] = cArr2[(b8 & 240) >> 4];
            cArr[i8 + 1] = cArr2[b8 & 15];
        }
        return new String(cArr);
    }

    public static native boolean isProposalValid(boolean z7, String str);

    public static InetAddress parseInetAddress(String str) {
        byte[] parseInetAddressBytes = parseInetAddressBytes(str);
        if (parseInetAddressBytes != null) {
            return InetAddress.getByAddress(parseInetAddressBytes);
        }
        throw new UnknownHostException();
    }

    private static native byte[] parseInetAddressBytes(String str);
}
