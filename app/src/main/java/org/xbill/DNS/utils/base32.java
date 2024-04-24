package org.xbill.DNS.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/utils/base32.class */
public class base32 {
    private String alphabet;
    private boolean lowercase;
    private boolean padding;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/utils/base32$Alphabet.class */
    public static class Alphabet {
        public static final String BASE32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567=";
        public static final String BASE32HEX = "0123456789ABCDEFGHIJKLMNOPQRSTUV=";

        private Alphabet() {
        }
    }

    public base32(String str, boolean z7, boolean z8) {
        this.alphabet = str;
        this.padding = z7;
        this.lowercase = z8;
    }

    private static int blockLenToPadding(int i7) {
        if (i7 == 1) {
            return 6;
        }
        if (i7 == 2) {
            return 4;
        }
        if (i7 == 3) {
            return 3;
        }
        if (i7 != 4) {
            return i7 != 5 ? -1 : 0;
        }
        return 1;
    }

    private static int paddingToBlockLen(int i7) {
        if (i7 == 0) {
            return 5;
        }
        if (i7 == 1) {
            return 4;
        }
        if (i7 == 3) {
            return 3;
        }
        if (i7 != 4) {
            return i7 != 6 ? -1 : 1;
        }
        return 2;
    }

    public byte[] fromString(String str) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (byte b8 : str.getBytes()) {
            char c = (char) b8;
            if (!Character.isWhitespace(c)) {
                byteArrayOutputStream.write((byte) Character.toUpperCase(c));
            }
        }
        if (!this.padding) {
            while (byteArrayOutputStream.size() % 8 != 0) {
                byteArrayOutputStream.write(61);
            }
        } else if (byteArrayOutputStream.size() % 8 != 0) {
            return null;
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.reset();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        for (int i7 = 0; i7 < byteArray.length / 8; i7++) {
            short[] sArr = new short[8];
            int i8 = 8;
            for (int i9 = 0; i9 < 8; i9++) {
                byte b9 = byteArray[(i7 * 8) + i9];
                if (((char) b9) == '=') {
                    break;
                }
                short indexOf = (short) this.alphabet.indexOf(b9);
                sArr[i9] = indexOf;
                if (indexOf < 0) {
                    return null;
                }
                i8--;
            }
            int paddingToBlockLen = paddingToBlockLen(i8);
            if (paddingToBlockLen < 0) {
                return null;
            }
            short s7 = sArr[0];
            short s8 = sArr[1];
            short s9 = sArr[2];
            short s10 = sArr[3];
            short s11 = sArr[4];
            short s12 = sArr[5];
            short s13 = sArr[6];
            short s14 = sArr[7];
            for (int i10 = 0; i10 < paddingToBlockLen; i10++) {
                try {
                    dataOutputStream.writeByte((byte) (new int[]{(s7 << 3) | (s8 >> 2), ((s8 & 3) << 6) | (s9 << 1) | (s10 >> 4), ((s10 & 15) << 4) | ((s11 >> 1) & 15), (s11 << 7) | (s12 << 2) | (s13 >> 3), s14 | ((s13 & 7) << 5)}[i10] & 255));
                } catch (IOException e8) {
                }
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    public String toString(byte[] bArr) {
        int i7;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i8 = 0; i8 < (bArr.length + 4) / 5; i8++) {
            short[] sArr = new short[5];
            int i9 = 5;
            for (int i10 = 0; i10 < 5; i10++) {
                int i11 = (i8 * 5) + i10;
                if (i11 < bArr.length) {
                    sArr[i10] = (short) (bArr[i11] & 255);
                } else {
                    sArr[i10] = 0;
                    i9--;
                }
            }
            int blockLenToPadding = blockLenToPadding(i9);
            short s7 = sArr[0];
            byte b8 = (byte) ((s7 >> 3) & 31);
            short s8 = sArr[1];
            byte b9 = (byte) (((s7 & 7) << 2) | ((s8 >> 6) & 3));
            byte b10 = (byte) ((s8 >> 1) & 31);
            short s9 = sArr[2];
            byte b11 = (byte) (((s8 & 1) << 4) | ((s9 >> 4) & 15));
            short s10 = sArr[3];
            byte b12 = (byte) (((s9 & 15) << 1) | (1 & (s10 >> 7)));
            byte b13 = (byte) ((s10 >> 2) & 31);
            short s11 = sArr[4];
            byte b14 = (byte) (((s11 >> 5) & 7) | ((s10 & 3) << 3));
            byte b15 = (byte) (s11 & 31);
            int i12 = 0;
            while (true) {
                i7 = 8 - blockLenToPadding;
                if (i12 >= i7) {
                    break;
                }
                char charAt = this.alphabet.charAt(new int[]{b8, b9, b10, b11, b12, b13, b14, b15}[i12]);
                char c = charAt;
                if (this.lowercase) {
                    c = Character.toLowerCase(charAt);
                }
                byteArrayOutputStream.write(c);
                i12++;
            }
            if (this.padding) {
                for (int i13 = i7; i13 < 8; i13++) {
                    byteArrayOutputStream.write(61);
                }
            }
        }
        return new String(byteArrayOutputStream.toByteArray());
    }
}
