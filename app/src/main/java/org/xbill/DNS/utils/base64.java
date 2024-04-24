package org.xbill.DNS.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/utils/base64.class */
public class base64 {
    private static final String Base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    private base64() {
    }

    public static String formatString(byte[] bArr, int i7, String str, boolean z7) {
        String str2;
        String base64Var = toString(bArr);
        StringBuffer stringBuffer = new StringBuffer();
        int i8 = 0;
        while (true) {
            int i9 = i8;
            if (i9 >= base64Var.length()) {
                return stringBuffer.toString();
            }
            stringBuffer.append(str);
            int i10 = i9 + i7;
            if (i10 >= base64Var.length()) {
                stringBuffer.append(base64Var.substring(i9));
                if (z7) {
                    str2 = " )";
                } else {
                    i8 = i10;
                }
            } else {
                stringBuffer.append(base64Var.substring(i9, i10));
                str2 = "\n";
            }
            stringBuffer.append(str2);
            i8 = i10;
        }
    }

    public static byte[] fromString(String str) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = str.getBytes();
        for (int i7 = 0; i7 < bytes.length; i7++) {
            if (!Character.isWhitespace((char) bytes[i7])) {
                byteArrayOutputStream.write(bytes[i7]);
            }
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        if (byteArray.length % 4 != 0) {
            return null;
        }
        byteArrayOutputStream.reset();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        for (int i8 = 0; i8 < (byteArray.length + 3) / 4; i8++) {
            short[] sArr = new short[4];
            short[] sArr2 = new short[3];
            for (int i9 = 0; i9 < 4; i9++) {
                sArr[i9] = (short) Base64.indexOf(byteArray[(i8 * 4) + i9]);
            }
            short s7 = sArr[0];
            short s8 = sArr[1];
            sArr2[0] = (short) ((s7 << 2) + (s8 >> 4));
            short s9 = sArr[2];
            if (s9 == 64) {
                sArr2[2] = -1;
                sArr2[1] = -1;
                if ((sArr[1] & 15) != 0) {
                    return null;
                }
            } else {
                short s10 = sArr[3];
                if (s10 == 64) {
                    sArr2[1] = (short) (((s8 << 4) + (s9 >> 2)) & 255);
                    sArr2[2] = -1;
                    if ((sArr[2] & 3) != 0) {
                        return null;
                    }
                } else {
                    sArr2[1] = (short) (((s8 << 4) + (s9 >> 2)) & 255);
                    sArr2[2] = (short) (((s9 << 6) + s10) & 255);
                }
            }
            for (int i10 = 0; i10 < 3; i10++) {
                short s11 = sArr2[i10];
                if (s11 >= 0) {
                    try {
                        dataOutputStream.writeByte(s11);
                    } catch (IOException e8) {
                    }
                }
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static String toString(byte[] bArr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i7 = 0; i7 < (bArr.length + 2) / 3; i7++) {
            short[] sArr = new short[3];
            short[] sArr2 = new short[4];
            for (int i8 = 0; i8 < 3; i8++) {
                int i9 = (i7 * 3) + i8;
                if (i9 < bArr.length) {
                    sArr[i8] = (short) (bArr[i9] & 255);
                } else {
                    sArr[i8] = -1;
                }
            }
            sArr2[0] = (short) (sArr[0] >> 2);
            short s7 = sArr[1];
            if (s7 == -1) {
                sArr2[1] = (short) ((sArr[0] & 3) << 4);
            } else {
                sArr2[1] = (short) (((sArr[0] & 3) << 4) + (s7 >> 4));
            }
            short s8 = sArr[1];
            if (s8 == -1) {
                sArr2[3] = 64;
                sArr2[2] = 64;
            } else {
                short s9 = sArr[2];
                if (s9 == -1) {
                    sArr2[2] = (short) ((s8 & 15) << 2);
                    sArr2[3] = 64;
                } else {
                    sArr2[2] = (short) (((s8 & 15) << 2) + (s9 >> 6));
                    sArr2[3] = (short) (sArr[2] & 63);
                }
            }
            for (int i10 = 0; i10 < 4; i10++) {
                byteArrayOutputStream.write(Base64.charAt(sArr2[i10]));
            }
        }
        return new String(byteArrayOutputStream.toByteArray());
    }
}
