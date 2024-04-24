package org.xbill.DNS;

import org.xbill.DNS.utils.base16;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DLVRecord.class */
public class DLVRecord extends Record {
    public static final int SHA1_DIGEST_ID = 1;
    public static final int SHA256_DIGEST_ID = 1;
    private static final long serialVersionUID = 1960742375677534148L;
    private int alg;
    private byte[] digest;
    private int digestid;
    private int footprint;

    public DLVRecord() {
    }

    public DLVRecord(Name name, int i7, long j7, int i8, int i9, int i10, byte[] bArr) {
        super(name, Type.DLV, i7, j7);
        this.footprint = Record.checkU16("footprint", i8);
        this.alg = Record.checkU8("alg", i9);
        this.digestid = Record.checkU8("digestid", i10);
        this.digest = bArr;
    }

    public int getAlgorithm() {
        return this.alg;
    }

    public byte[] getDigest() {
        return this.digest;
    }

    public int getDigestID() {
        return this.digestid;
    }

    public int getFootprint() {
        return this.footprint;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new DLVRecord();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.footprint = tokenizer.getUInt16();
        this.alg = tokenizer.getUInt8();
        this.digestid = tokenizer.getUInt8();
        this.digest = tokenizer.getHex();
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.footprint = dNSInput.readU16();
        this.alg = dNSInput.readU8();
        this.digestid = dNSInput.readU8();
        this.digest = dNSInput.readByteArray();
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.footprint);
        stringBuffer.append(" ");
        stringBuffer.append(this.alg);
        stringBuffer.append(" ");
        stringBuffer.append(this.digestid);
        if (this.digest != null) {
            stringBuffer.append(" ");
            stringBuffer.append(base16.toString(this.digest));
        }
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU16(this.footprint);
        dNSOutput.writeU8(this.alg);
        dNSOutput.writeU8(this.digestid);
        byte[] bArr = this.digest;
        if (bArr != null) {
            dNSOutput.writeByteArray(bArr);
        }
    }
}
