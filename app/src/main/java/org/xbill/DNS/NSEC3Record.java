package org.xbill.DNS;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.strongswan.android.data.VpnProfileDataSource;
import org.xbill.DNS.utils.base16;
import org.xbill.DNS.utils.base32;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/NSEC3Record.class */
public class NSEC3Record extends Record {
    public static final int SHA1_DIGEST_ID = 1;
    private static final base32 b32 = new base32(base32.Alphabet.BASE32HEX, false, false);
    private static final long serialVersionUID = -7123504635968932855L;
    private int flags;
    private int hashAlg;
    private int iterations;
    private byte[] next;
    private byte[] salt;
    private TypeBitmap types;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/NSEC3Record$Digest.class */
    public static class Digest {
        public static final int SHA1 = 1;

        private Digest() {
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/NSEC3Record$Flags.class */
    public static class Flags {
        public static final int OPT_OUT = 1;

        private Flags() {
        }
    }

    public NSEC3Record() {
    }

    public NSEC3Record(Name name, int i7, long j7, int i8, int i9, int i10, byte[] bArr, byte[] bArr2, int[] iArr) {
        super(name, 50, i7, j7);
        this.hashAlg = Record.checkU8("hashAlg", i8);
        this.flags = Record.checkU8(VpnProfileDataSource.KEY_FLAGS, i9);
        this.iterations = Record.checkU16("iterations", i10);
        if (bArr != null) {
            if (bArr.length > 255) {
                throw new IllegalArgumentException("Invalid salt");
            }
            if (bArr.length > 0) {
                byte[] bArr3 = new byte[bArr.length];
                this.salt = bArr3;
                System.arraycopy(bArr, 0, bArr3, 0, bArr.length);
            }
        }
        if (bArr2.length > 255) {
            throw new IllegalArgumentException("Invalid next hash");
        }
        byte[] bArr4 = new byte[bArr2.length];
        this.next = bArr4;
        System.arraycopy(bArr2, 0, bArr4, 0, bArr2.length);
        this.types = new TypeBitmap(iArr);
    }

    public static byte[] hashName(Name name, int i7, int i8, byte[] bArr) {
        if (i7 != 1) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Unknown NSEC3 algorithmidentifier: ");
            stringBuffer.append(i7);
            throw new NoSuchAlgorithmException(stringBuffer.toString());
        }
        MessageDigest messageDigest = MessageDigest.getInstance("sha-1");
        byte[] bArr2 = null;
        for (int i9 = 0; i9 <= i8; i9++) {
            messageDigest.reset();
            if (i9 == 0) {
                messageDigest.update(name.toWireCanonical());
            } else {
                messageDigest.update(bArr2);
            }
            if (bArr != null) {
                messageDigest.update(bArr);
            }
            bArr2 = messageDigest.digest();
        }
        return bArr2;
    }

    public int getFlags() {
        return this.flags;
    }

    public int getHashAlgorithm() {
        return this.hashAlg;
    }

    public int getIterations() {
        return this.iterations;
    }

    public byte[] getNext() {
        return this.next;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new NSEC3Record();
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public int[] getTypes() {
        return this.types.toArray();
    }

    public boolean hasType(int i7) {
        return this.types.contains(i7);
    }

    public byte[] hashName(Name name) {
        return hashName(name, this.hashAlg, this.iterations, this.salt);
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.hashAlg = tokenizer.getUInt8();
        this.flags = tokenizer.getUInt8();
        this.iterations = tokenizer.getUInt16();
        if (tokenizer.getString().equals("-")) {
            this.salt = null;
        } else {
            tokenizer.unget();
            byte[] hexString = tokenizer.getHexString();
            this.salt = hexString;
            if (hexString.length > 255) {
                throw tokenizer.exception("salt value too long");
            }
        }
        this.next = tokenizer.getBase32String(b32);
        this.types = new TypeBitmap(tokenizer);
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.hashAlg = dNSInput.readU8();
        this.flags = dNSInput.readU8();
        this.iterations = dNSInput.readU16();
        int readU8 = dNSInput.readU8();
        if (readU8 > 0) {
            this.salt = dNSInput.readByteArray(readU8);
        } else {
            this.salt = null;
        }
        this.next = dNSInput.readByteArray(dNSInput.readU8());
        this.types = new TypeBitmap(dNSInput);
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.hashAlg);
        stringBuffer.append(' ');
        stringBuffer.append(this.flags);
        stringBuffer.append(' ');
        stringBuffer.append(this.iterations);
        stringBuffer.append(' ');
        byte[] bArr = this.salt;
        if (bArr == null) {
            stringBuffer.append('-');
        } else {
            stringBuffer.append(base16.toString(bArr));
        }
        stringBuffer.append(' ');
        stringBuffer.append(b32.toString(this.next));
        if (!this.types.empty()) {
            stringBuffer.append(' ');
            stringBuffer.append(this.types.toString());
        }
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU8(this.hashAlg);
        dNSOutput.writeU8(this.flags);
        dNSOutput.writeU16(this.iterations);
        byte[] bArr = this.salt;
        if (bArr != null) {
            dNSOutput.writeU8(bArr.length);
            dNSOutput.writeByteArray(this.salt);
        } else {
            dNSOutput.writeU8(0);
        }
        dNSOutput.writeU8(this.next.length);
        dNSOutput.writeByteArray(this.next);
        this.types.toWire(dNSOutput);
    }
}
