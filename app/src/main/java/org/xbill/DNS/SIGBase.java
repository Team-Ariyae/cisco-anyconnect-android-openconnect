package org.xbill.DNS;

import androidx.activity.result.a;
import java.util.Date;
import org.xbill.DNS.DNSSEC;
import org.xbill.DNS.utils.base64;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/SIGBase.class */
public abstract class SIGBase extends Record {
    private static final long serialVersionUID = -3738444391533812369L;
    public int alg;
    public int covered;
    public Date expire;
    public int footprint;
    public int labels;
    public long origttl;
    public byte[] signature;
    public Name signer;
    public Date timeSigned;

    public SIGBase() {
    }

    public SIGBase(Name name, int i7, int i8, long j7, int i9, int i10, long j8, Date date, Date date2, int i11, Name name2, byte[] bArr) {
        super(name, i7, i8, j7);
        Type.check(i9);
        TTL.check(j8);
        this.covered = i9;
        this.alg = Record.checkU8("alg", i10);
        this.labels = name.labels() - 1;
        if (name.isWild()) {
            this.labels--;
        }
        this.origttl = j8;
        this.expire = date;
        this.timeSigned = date2;
        this.footprint = Record.checkU16("footprint", i11);
        this.signer = Record.checkName("signer", name2);
        this.signature = bArr;
    }

    public int getAlgorithm() {
        return this.alg;
    }

    public Date getExpire() {
        return this.expire;
    }

    public int getFootprint() {
        return this.footprint;
    }

    public int getLabels() {
        return this.labels;
    }

    public long getOrigTTL() {
        return this.origttl;
    }

    public byte[] getSignature() {
        return this.signature;
    }

    public Name getSigner() {
        return this.signer;
    }

    public Date getTimeSigned() {
        return this.timeSigned;
    }

    public int getTypeCovered() {
        return this.covered;
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        String string = tokenizer.getString();
        int value = Type.value(string);
        this.covered = value;
        if (value < 0) {
            throw a.u("Invalid type: ", string, tokenizer);
        }
        String string2 = tokenizer.getString();
        int value2 = DNSSEC.Algorithm.value(string2);
        this.alg = value2;
        if (value2 < 0) {
            throw a.u("Invalid algorithm: ", string2, tokenizer);
        }
        this.labels = tokenizer.getUInt8();
        this.origttl = tokenizer.getTTL();
        this.expire = FormattedTime.parse(tokenizer.getString());
        this.timeSigned = FormattedTime.parse(tokenizer.getString());
        this.footprint = tokenizer.getUInt16();
        this.signer = tokenizer.getName(name);
        this.signature = tokenizer.getBase64();
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.covered = dNSInput.readU16();
        this.alg = dNSInput.readU8();
        this.labels = dNSInput.readU8();
        this.origttl = dNSInput.readU32();
        this.expire = new Date(dNSInput.readU32() * 1000);
        this.timeSigned = new Date(dNSInput.readU32() * 1000);
        this.footprint = dNSInput.readU16();
        this.signer = new Name(dNSInput);
        this.signature = dNSInput.readByteArray();
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        String base64Var;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Type.string(this.covered));
        stringBuffer.append(" ");
        stringBuffer.append(this.alg);
        stringBuffer.append(" ");
        stringBuffer.append(this.labels);
        stringBuffer.append(" ");
        stringBuffer.append(this.origttl);
        stringBuffer.append(" ");
        if (Options.check("multiline")) {
            stringBuffer.append("(\n\t");
        }
        stringBuffer.append(FormattedTime.format(this.expire));
        stringBuffer.append(" ");
        stringBuffer.append(FormattedTime.format(this.timeSigned));
        stringBuffer.append(" ");
        stringBuffer.append(this.footprint);
        stringBuffer.append(" ");
        stringBuffer.append(this.signer);
        if (Options.check("multiline")) {
            stringBuffer.append("\n");
            base64Var = base64.formatString(this.signature, 64, "\t", true);
        } else {
            stringBuffer.append(" ");
            base64Var = base64.toString(this.signature);
        }
        stringBuffer.append(base64Var);
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU16(this.covered);
        dNSOutput.writeU8(this.alg);
        dNSOutput.writeU8(this.labels);
        dNSOutput.writeU32(this.origttl);
        dNSOutput.writeU32(this.expire.getTime() / 1000);
        dNSOutput.writeU32(this.timeSigned.getTime() / 1000);
        dNSOutput.writeU16(this.footprint);
        this.signer.toWire(dNSOutput, null, z7);
        dNSOutput.writeByteArray(this.signature);
    }

    public void setSignature(byte[] bArr) {
        this.signature = bArr;
    }
}
