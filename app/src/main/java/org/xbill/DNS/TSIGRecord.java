package org.xbill.DNS;

import java.util.Date;
import org.xbill.DNS.utils.base64;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/TSIGRecord.class */
public class TSIGRecord extends Record {
    private static final long serialVersionUID = -88820909016649306L;
    private Name alg;
    private int error;
    private int fudge;
    private int originalID;
    private byte[] other;
    private byte[] signature;
    private Date timeSigned;

    public TSIGRecord() {
    }

    public TSIGRecord(Name name, int i7, long j7, Name name2, Date date, int i8, byte[] bArr, int i9, int i10, byte[] bArr2) {
        super(name, Type.TSIG, i7, j7);
        this.alg = Record.checkName("alg", name2);
        this.timeSigned = date;
        this.fudge = Record.checkU16("fudge", i8);
        this.signature = bArr;
        this.originalID = Record.checkU16("originalID", i9);
        this.error = Record.checkU16("error", i10);
        this.other = bArr2;
    }

    public Name getAlgorithm() {
        return this.alg;
    }

    public int getError() {
        return this.error;
    }

    public int getFudge() {
        return this.fudge;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new TSIGRecord();
    }

    public int getOriginalID() {
        return this.originalID;
    }

    public byte[] getOther() {
        return this.other;
    }

    public byte[] getSignature() {
        return this.signature;
    }

    public Date getTimeSigned() {
        return this.timeSigned;
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        throw tokenizer.exception("no text format defined for TSIG");
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.alg = new Name(dNSInput);
        this.timeSigned = new Date(((dNSInput.readU16() << 32) + dNSInput.readU32()) * 1000);
        this.fudge = dNSInput.readU16();
        this.signature = dNSInput.readByteArray(dNSInput.readU16());
        this.originalID = dNSInput.readU16();
        this.error = dNSInput.readU16();
        int readU16 = dNSInput.readU16();
        if (readU16 > 0) {
            this.other = dNSInput.readByteArray(readU16);
        } else {
            this.other = null;
        }
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        String base64Var;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.alg);
        stringBuffer.append(" ");
        if (Options.check("multiline")) {
            stringBuffer.append("(\n\t");
        }
        stringBuffer.append(this.timeSigned.getTime() / 1000);
        stringBuffer.append(" ");
        stringBuffer.append(this.fudge);
        stringBuffer.append(" ");
        stringBuffer.append(this.signature.length);
        if (Options.check("multiline")) {
            stringBuffer.append("\n");
            base64Var = base64.formatString(this.signature, 64, "\t", false);
        } else {
            stringBuffer.append(" ");
            base64Var = base64.toString(this.signature);
        }
        stringBuffer.append(base64Var);
        stringBuffer.append(" ");
        stringBuffer.append(Rcode.TSIGstring(this.error));
        stringBuffer.append(" ");
        byte[] bArr = this.other;
        if (bArr == null) {
            stringBuffer.append(0);
        } else {
            stringBuffer.append(bArr.length);
            stringBuffer.append(Options.check("multiline") ? "\n\n\n\t" : " ");
            if (this.error == 18) {
                byte[] bArr2 = this.other;
                if (bArr2.length != 6) {
                    stringBuffer.append("<invalid BADTIME other data>");
                } else {
                    long j7 = bArr2[0] & 255;
                    long j8 = bArr2[1] & 255;
                    long j9 = (bArr2[2] & 255) << 24;
                    long j10 = (bArr2[3] & 255) << 16;
                    long j11 = (bArr2[4] & 255) << 8;
                    long j12 = bArr2[5] & 255;
                    stringBuffer.append("<server time: ");
                    stringBuffer.append(new Date(((j7 << 40) + (j8 << 32) + j9 + j10 + j11 + j12) * 1000));
                }
            } else {
                stringBuffer.append("<");
                stringBuffer.append(base64.toString(this.other));
            }
            stringBuffer.append(">");
        }
        if (Options.check("multiline")) {
            stringBuffer.append(" )");
        }
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        this.alg.toWire(dNSOutput, null, z7);
        long time = this.timeSigned.getTime() / 1000;
        dNSOutput.writeU16((int) (time >> 32));
        dNSOutput.writeU32(time & 4294967295L);
        dNSOutput.writeU16(this.fudge);
        dNSOutput.writeU16(this.signature.length);
        dNSOutput.writeByteArray(this.signature);
        dNSOutput.writeU16(this.originalID);
        dNSOutput.writeU16(this.error);
        byte[] bArr = this.other;
        if (bArr == null) {
            dNSOutput.writeU16(0);
        } else {
            dNSOutput.writeU16(bArr.length);
            dNSOutput.writeByteArray(this.other);
        }
    }
}
