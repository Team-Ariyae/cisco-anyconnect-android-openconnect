package org.xbill.DNS;

import org.strongswan.android.data.VpnProfileDataSource;
import org.xbill.DNS.utils.base16;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/NSEC3PARAMRecord.class */
public class NSEC3PARAMRecord extends Record {
    private static final long serialVersionUID = -8689038598776316533L;
    private int flags;
    private int hashAlg;
    private int iterations;
    private byte[] salt;

    public NSEC3PARAMRecord() {
    }

    public NSEC3PARAMRecord(Name name, int i7, long j7, int i8, int i9, int i10, byte[] bArr) {
        super(name, 51, i7, j7);
        this.hashAlg = Record.checkU8("hashAlg", i8);
        this.flags = Record.checkU8(VpnProfileDataSource.KEY_FLAGS, i9);
        this.iterations = Record.checkU16("iterations", i10);
        if (bArr != null) {
            if (bArr.length > 255) {
                throw new IllegalArgumentException("Invalid salt length");
            }
            if (bArr.length > 0) {
                byte[] bArr2 = new byte[bArr.length];
                this.salt = bArr2;
                System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
            }
        }
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

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new NSEC3PARAMRecord();
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public byte[] hashName(Name name) {
        return NSEC3Record.hashName(name, this.hashAlg, this.iterations, this.salt);
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.hashAlg = tokenizer.getUInt8();
        this.flags = tokenizer.getUInt8();
        this.iterations = tokenizer.getUInt16();
        if (tokenizer.getString().equals("-")) {
            this.salt = null;
            return;
        }
        tokenizer.unget();
        byte[] hexString = tokenizer.getHexString();
        this.salt = hexString;
        if (hexString.length > 255) {
            throw tokenizer.exception("salt value too long");
        }
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
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU8(this.hashAlg);
        dNSOutput.writeU8(this.flags);
        dNSOutput.writeU16(this.iterations);
        byte[] bArr = this.salt;
        if (bArr == null) {
            dNSOutput.writeU8(0);
        } else {
            dNSOutput.writeU8(bArr.length);
            dNSOutput.writeByteArray(this.salt);
        }
    }
}
