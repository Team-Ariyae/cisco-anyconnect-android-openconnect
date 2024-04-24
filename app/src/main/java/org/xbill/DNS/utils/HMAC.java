package org.xbill.DNS.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/utils/HMAC.class */
public class HMAC {
    private static final byte IPAD = 54;
    private static final byte OPAD = 92;
    private int blockLength;
    private MessageDigest digest;
    private byte[] ipad;
    private byte[] opad;

    public HMAC(String str, int i7, byte[] bArr) {
        try {
            this.digest = MessageDigest.getInstance(str);
            this.blockLength = i7;
            init(bArr);
        } catch (NoSuchAlgorithmException e8) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("unknown digest algorithm ");
            stringBuffer.append(str);
            throw new IllegalArgumentException(stringBuffer.toString());
        }
    }

    public HMAC(String str, byte[] bArr) {
        this(str, 64, bArr);
    }

    public HMAC(MessageDigest messageDigest, int i7, byte[] bArr) {
        messageDigest.reset();
        this.digest = messageDigest;
        this.blockLength = i7;
        init(bArr);
    }

    public HMAC(MessageDigest messageDigest, byte[] bArr) {
        this(messageDigest, 64, bArr);
    }

    private void init(byte[] bArr) {
        int i7;
        byte[] bArr2 = bArr;
        if (bArr.length > this.blockLength) {
            bArr2 = this.digest.digest(bArr);
            this.digest.reset();
        }
        int i8 = this.blockLength;
        this.ipad = new byte[i8];
        this.opad = new byte[i8];
        int i9 = 0;
        while (true) {
            if (i9 >= bArr2.length) {
                break;
            }
            this.ipad[i9] = (byte) (IPAD ^ bArr2[i9]);
            this.opad[i9] = (byte) (OPAD ^ bArr2[i9]);
            i9++;
        }
        for (i7 = i9; i7 < this.blockLength; i7++) {
            this.ipad[i7] = IPAD;
            this.opad[i7] = OPAD;
        }
        this.digest.update(this.ipad);
    }

    public void clear() {
        this.digest.reset();
        this.digest.update(this.ipad);
    }

    public int digestLength() {
        return this.digest.getDigestLength();
    }

    public byte[] sign() {
        byte[] digest = this.digest.digest();
        this.digest.reset();
        this.digest.update(this.opad);
        return this.digest.digest(digest);
    }

    public void update(byte[] bArr) {
        this.digest.update(bArr);
    }

    public void update(byte[] bArr, int i7, int i8) {
        this.digest.update(bArr, i7, i8);
    }

    public boolean verify(byte[] bArr) {
        return verify(bArr, false);
    }

    public boolean verify(byte[] bArr, boolean z7) {
        byte[] sign = sign();
        byte[] bArr2 = sign;
        if (z7) {
            bArr2 = sign;
            if (bArr.length < sign.length) {
                int length = bArr.length;
                bArr2 = new byte[length];
                System.arraycopy(sign, 0, bArr2, 0, length);
            }
        }
        return Arrays.equals(bArr, bArr2);
    }
}
