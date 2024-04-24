package org.xbill.DNS;

import org.strongswan.android.logic.CharonVpnService;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/SOARecord.class */
public class SOARecord extends Record {
    private static final long serialVersionUID = 1049740098229303931L;
    private Name admin;
    private long expire;
    private Name host;
    private long minimum;
    private long refresh;
    private long retry;
    private long serial;

    public SOARecord() {
    }

    public SOARecord(Name name, int i7, long j7, Name name2, Name name3, long j8, long j9, long j10, long j11, long j12) {
        super(name, 6, i7, j7);
        this.host = Record.checkName("host", name2);
        this.admin = Record.checkName("admin", name3);
        this.serial = Record.checkU32("serial", j8);
        this.refresh = Record.checkU32("refresh", j9);
        this.retry = Record.checkU32(CharonVpnService.KEY_IS_RETRY, j10);
        this.expire = Record.checkU32("expire", j11);
        this.minimum = Record.checkU32("minimum", j12);
    }

    public Name getAdmin() {
        return this.admin;
    }

    public long getExpire() {
        return this.expire;
    }

    public Name getHost() {
        return this.host;
    }

    public long getMinimum() {
        return this.minimum;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new SOARecord();
    }

    public long getRefresh() {
        return this.refresh;
    }

    public long getRetry() {
        return this.retry;
    }

    public long getSerial() {
        return this.serial;
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.host = tokenizer.getName(name);
        this.admin = tokenizer.getName(name);
        this.serial = tokenizer.getUInt32();
        this.refresh = tokenizer.getTTLLike();
        this.retry = tokenizer.getTTLLike();
        this.expire = tokenizer.getTTLLike();
        this.minimum = tokenizer.getTTLLike();
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.host = new Name(dNSInput);
        this.admin = new Name(dNSInput);
        this.serial = dNSInput.readU32();
        this.refresh = dNSInput.readU32();
        this.retry = dNSInput.readU32();
        this.expire = dNSInput.readU32();
        this.minimum = dNSInput.readU32();
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.host);
        stringBuffer.append(" ");
        stringBuffer.append(this.admin);
        if (Options.check("multiline")) {
            stringBuffer.append(" (\n\t\t\t\t\t");
            stringBuffer.append(this.serial);
            stringBuffer.append("\t; serial\n\t\t\t\t\t");
            stringBuffer.append(this.refresh);
            stringBuffer.append("\t; refresh\n\t\t\t\t\t");
            stringBuffer.append(this.retry);
            stringBuffer.append("\t; retry\n\t\t\t\t\t");
            stringBuffer.append(this.expire);
            stringBuffer.append("\t; expire\n\t\t\t\t\t");
            stringBuffer.append(this.minimum);
            stringBuffer.append(" )\t; minimum");
        } else {
            stringBuffer.append(" ");
            stringBuffer.append(this.serial);
            stringBuffer.append(" ");
            stringBuffer.append(this.refresh);
            stringBuffer.append(" ");
            stringBuffer.append(this.retry);
            stringBuffer.append(" ");
            stringBuffer.append(this.expire);
            stringBuffer.append(" ");
            stringBuffer.append(this.minimum);
        }
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        this.host.toWire(dNSOutput, compression, z7);
        this.admin.toWire(dNSOutput, compression, z7);
        dNSOutput.writeU32(this.serial);
        dNSOutput.writeU32(this.refresh);
        dNSOutput.writeU32(this.retry);
        dNSOutput.writeU32(this.expire);
        dNSOutput.writeU32(this.minimum);
    }
}
