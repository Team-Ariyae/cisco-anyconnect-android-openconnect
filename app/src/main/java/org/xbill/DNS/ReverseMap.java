package org.xbill.DNS;

import java.net.InetAddress;
import java.net.UnknownHostException;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/ReverseMap.class */
public final class ReverseMap {
    private static Name inaddr4 = Name.fromConstantString("in-addr.arpa.");
    private static Name inaddr6 = Name.fromConstantString("ip6.arpa.");

    private ReverseMap() {
    }

    public static Name fromAddress(String str) {
        byte[] byteArray = Address.toByteArray(str, 1);
        byte[] bArr = byteArray;
        if (byteArray == null) {
            bArr = Address.toByteArray(str, 2);
        }
        if (bArr != null) {
            return fromAddress(bArr);
        }
        throw new UnknownHostException("Invalid IP address");
    }

    public static Name fromAddress(String str, int i7) {
        byte[] byteArray = Address.toByteArray(str, i7);
        if (byteArray != null) {
            return fromAddress(byteArray);
        }
        throw new UnknownHostException("Invalid IP address");
    }

    public static Name fromAddress(InetAddress inetAddress) {
        return fromAddress(inetAddress.getAddress());
    }

    public static Name fromAddress(byte[] bArr) {
        if (bArr.length != 4 && bArr.length != 16) {
            throw new IllegalArgumentException("array must contain 4 or 16 elements");
        }
        StringBuffer stringBuffer = new StringBuffer();
        if (bArr.length == 4) {
            for (int length = bArr.length - 1; length >= 0; length--) {
                stringBuffer.append(bArr[length] & 255);
                if (length > 0) {
                    stringBuffer.append(".");
                }
            }
        } else {
            int[] iArr = new int[2];
            for (int length2 = bArr.length - 1; length2 >= 0; length2--) {
                byte b8 = bArr[length2];
                iArr[0] = (b8 & 255) >> 4;
                iArr[1] = b8 & 255 & 15;
                for (int i7 = 1; i7 >= 0; i7--) {
                    stringBuffer.append(Integer.toHexString(iArr[i7]));
                    if (length2 > 0 || i7 > 0) {
                        stringBuffer.append(".");
                    }
                }
            }
        }
        try {
            return bArr.length == 4 ? Name.fromString(stringBuffer.toString(), inaddr4) : Name.fromString(stringBuffer.toString(), inaddr6);
        } catch (TextParseException e8) {
            throw new IllegalStateException("name cannot be invalid");
        }
    }

    public static Name fromAddress(int[] iArr) {
        byte[] bArr = new byte[iArr.length];
        for (int i7 = 0; i7 < iArr.length; i7++) {
            int i8 = iArr[i7];
            if (i8 < 0 || i8 > 255) {
                throw new IllegalArgumentException("array must contain values between 0 and 255");
            }
            bArr[i7] = (byte) i8;
        }
        return fromAddress(bArr);
    }
}
