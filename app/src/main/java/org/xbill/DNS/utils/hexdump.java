package org.xbill.DNS.utils;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/utils/hexdump.class */
public class hexdump {
    private static final char[] hex = "0123456789ABCDEF".toCharArray();

    public static String dump(String str, byte[] bArr) {
        return dump(str, bArr, 0, bArr.length);
    }

    public static String dump(String str, byte[] bArr, int i7, int i8) {
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append(i8);
        stringBuffer2.append("b");
        stringBuffer.append(stringBuffer2.toString());
        if (str != null) {
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append(" (");
            stringBuffer3.append(str);
            stringBuffer3.append(")");
            stringBuffer.append(stringBuffer3.toString());
        }
        stringBuffer.append(':');
        int length = (stringBuffer.toString().length() + 8) & (-8);
        stringBuffer.append('\t');
        int i9 = (80 - length) / 3;
        for (int i10 = 0; i10 < i8; i10++) {
            if (i10 != 0 && i10 % i9 == 0) {
                stringBuffer.append('\n');
                for (int i11 = 0; i11 < length / 8; i11++) {
                    stringBuffer.append('\t');
                }
            }
            int i12 = bArr[i10 + i7] & 255;
            char[] cArr = hex;
            stringBuffer.append(cArr[i12 >> 4]);
            stringBuffer.append(cArr[i12 & 15]);
            stringBuffer.append(' ');
        }
        stringBuffer.append('\n');
        return stringBuffer.toString();
    }
}
